package bookingapp.service.impl;

import bookingapp.dto.accommodation.AccommodationDto;
import bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import bookingapp.exception.EntityNotFoundException;
import bookingapp.mapper.AccommodationMapper;
import bookingapp.model.Accommodation;
import bookingapp.repository.AccommodationRepository;
import bookingapp.service.AccommodationService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;

    public List<AccommodationDto> findAll(Pageable pageable) {
        Page<Accommodation> accommodations = accommodationRepository.findAll(pageable);
        return accommodations.stream()
                .map(accommodationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AccommodationDto findById(Long id) {
        Accommodation accommodationById = findAccommodationById(id);
        return accommodationMapper.toDto(accommodationById);
    }

    private Accommodation findAccommodationById(Long id) {
        return accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find accommodation by id " + id));
    }

    @Override
    public AccommodationDto createAccommodation(
            CreateAccommodationRequestDto createAccommodationRequestDto) {
        Accommodation newAccommodation =
                accommodationMapper.toEntity(createAccommodationRequestDto);
        return accommodationMapper.toDto(accommodationRepository.save(newAccommodation));
    }

    @Override
    public AccommodationDto updateAccommodation(
            Long id,
            CreateAccommodationRequestDto updateAccommodationRequestDto) {
        Accommodation updatedAccommodation =
                accommodationMapper.toEntity(updateAccommodationRequestDto);
        updatedAccommodation.setId(id);
        return accommodationMapper.toDto(accommodationRepository.save(updatedAccommodation));
    }

    @Override
    public void deleteById(Long id) {
        accommodationRepository.deleteById(id);
    }
}
