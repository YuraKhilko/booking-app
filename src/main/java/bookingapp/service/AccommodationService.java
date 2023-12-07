package bookingapp.service;

import bookingapp.dto.accommodation.AccommodationDto;
import bookingapp.dto.accommodation.MergeAccommodationRequestDto;
import bookingapp.model.Accommodation;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface AccommodationService {
    List<AccommodationDto> findAll(Pageable pageable);

    AccommodationDto findById(Long id);

    AccommodationDto createAccommodation(
            MergeAccommodationRequestDto mergeAccommodationRequestDto);

    AccommodationDto updateAccommodation(
            Long id, MergeAccommodationRequestDto mergeAccommodationRequestDto);

    void deleteById(Long id);

    Accommodation findAccommodationById(Long id);

    boolean isAccommodationAvailable(Long id);
}
