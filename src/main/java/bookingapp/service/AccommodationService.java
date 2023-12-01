package bookingapp.service;

import bookingapp.dto.accommodation.AccommodationDto;
import bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface AccommodationService {
    List<AccommodationDto> findAll(Pageable pageable);

    AccommodationDto findById(Long id);

    AccommodationDto createAccommodation(
            CreateAccommodationRequestDto createAccommodationRequestDto);

    AccommodationDto updateAccommodation(
            Long id, CreateAccommodationRequestDto updateAccommodationRequestDto);

    void deleteById(Long id);
}
