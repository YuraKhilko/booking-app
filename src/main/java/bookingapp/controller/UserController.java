package bookingapp.controller;

import bookingapp.dto.user.UserResponseDto;
import bookingapp.dto.user.UserUpdateRequestDto;
import bookingapp.model.User;
import bookingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Managing users information",
        description = "Allows users to get and update their profiles information")
@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get current user profile",
            description = "Get current user profile")
    @GetMapping("/me")
    public UserResponseDto getUserProfile(Authentication authentication) {
        User user = getUserByAuthentication(authentication);
        return userService.getById(user.getId());
    }

    @Operation(summary = "Update current user profile",
            description = "Update current user profile")
    @PatchMapping ("/me")
    public UserResponseDto updateUserProfile(
            Authentication authentication,
            @RequestBody @Valid UserUpdateRequestDto updateRequestDto) {
        User user = getUserByAuthentication(authentication);
        return userService.updateById(user.getId(), updateRequestDto);
    }


    private User getUserByAuthentication(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
