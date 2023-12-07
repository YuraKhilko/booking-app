package bookingapp.service.impl;

import bookingapp.dto.accommodation.AccommodationDto;
import bookingapp.dto.accommodation.MergeAccommodationRequestDto;
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

    @Override
    public AccommodationDto createAccommodation(
            MergeAccommodationRequestDto mergeAccommodationRequestDto) {
        Accommodation newAccommodation =
                accommodationMapper.toEntity(mergeAccommodationRequestDto);
        return accommodationMapper.toDto(accommodationRepository.save(newAccommodation));
    }

    @Override
    public AccommodationDto updateAccommodation(
            Long id,
            MergeAccommodationRequestDto mergeAccommodationRequestDto) {
        Accommodation sourceAccommodation =
                accommodationMapper.toEntity(mergeAccommodationRequestDto);
        Accommodation accommodationForUpdate = findAccommodationById(id);

        updateAccommodationWithRequest(accommodationForUpdate, sourceAccommodation);

        return accommodationMapper.toDto(accommodationRepository.save(accommodationForUpdate));
    }

    public Accommodation findAccommodationById(Long id) {
        return accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find accommodation by id " + id));
    }

    @Override
    public boolean isAccommodationAvailable(Long id) {
        Accommodation accommodation = findAccommodationById(id);
        if (accommodation.isDeleted()) {
            throw new EntityNotFoundException("Accommodation with id " + id
                    + " is not available anymore");
        }
        return true;
    }

    private void updateAccommodationWithRequest(
            Accommodation destinationAccommodation,
            Accommodation sourceAccommodation) {
        destinationAccommodation.setSize(sourceAccommodation.getSize());
        destinationAccommodation.setDailyRate(sourceAccommodation.getDailyRate());
        destinationAccommodation.setAvailability(sourceAccommodation.getAvailability());
        destinationAccommodation.setAmenities(sourceAccommodation.getAmenities());
        destinationAccommodation.setType(sourceAccommodation.getType());
        destinationAccommodation.getAddress().setName(sourceAccommodation.getAddress().getName());
    }

    @Override
    public void deleteById(Long id) {
        accommodationRepository.deleteById(id);
    }
}
