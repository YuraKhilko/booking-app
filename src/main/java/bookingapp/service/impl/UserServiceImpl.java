package bookingapp.service.impl;

import bookingapp.dto.user.UserRegistrationRequestDto;
import bookingapp.dto.user.UserResponseDto;
import bookingapp.dto.user.UserUpdateRequestDto;
import bookingapp.exception.EntityNotFoundException;
import bookingapp.exception.RegistrationException;
import bookingapp.mapper.UserMapper;
import bookingapp.model.Role;
import bookingapp.model.User;
import bookingapp.repository.RoleRepository;
import bookingapp.repository.UserRepository;
import bookingapp.service.UserService;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Role.RoleName DEFAULT_ROLE = Role.RoleName.CUSTOMER;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (checkIfUserWithTheSameEmailPresent(requestDto.getEmail())) {
            User newUser = initializeNewUser(requestDto);
            userRepository.save(newUser);
            return userMapper.toResponseDto(newUser);
        }
        return null;
    }

    private boolean checkIfUserWithTheSameEmailPresent(String checkedEmail)
            throws RegistrationException {
        if (userRepository.findByEmail(checkedEmail).isPresent()) {
            throw new RegistrationException("Email has already been registered");
        }
        return true;
    }

    private User initializeNewUser(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        Set<Role> defaultUserRoleSet = getDefaultUserRoleSet();
        user.setRoles(defaultUserRoleSet);

        return user;
    }

    private Set<Role> getDefaultUserRoleSet() throws RegistrationException {
        Role defaultUserRole = roleRepository.findRoleByName(DEFAULT_ROLE)
                .orElseThrow(() -> new RegistrationException("Can't find Role by name"));
        Set<Role> defaultUserRoleSet = new HashSet<>();
        defaultUserRoleSet.add(defaultUserRole);
        return defaultUserRoleSet;
    }

    @Override
    public UserResponseDto getById(Long id) {
        User userFromDb = getUserFromDbById(id);
        return userMapper.toResponseDto(userFromDb);
    }

    @Override
    public UserResponseDto updateById(Long id, UserUpdateRequestDto updateRequestDto) {
        User userFromDb = getUserFromDbById(id);
        User userSource = userMapper.toModel(updateRequestDto);
        updateUserFromDb(userFromDb, userSource);
        return userMapper.toResponseDto(userFromDb);
    }

    private User getUserFromDbById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find User by id"));
    }

    private void updateUserFromDb(User userFromDb, User userSource) {
        userSource.setFirstName(userSource.getFirstName());
        userFromDb.setLastName(userSource.getLastName());
    }
}
