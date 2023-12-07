package bookingapp.controller;

import bookingapp.dto.accommodation.AccommodationDto;
import bookingapp.dto.accommodation.MergeAccommodationRequestDto;
import bookingapp.service.AccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Managing accommodation inventory",
        description = "Managing accommodation inventory")
@RestController
@RequestMapping(value = "/accommodations")
@RequiredArgsConstructor
public class AccommodationController {
    private final AccommodationService accommodationService;

    @Operation(summary = "Provides a list of available accommodations",
            description = "Provides a list of available accommodations")
    @GetMapping
    public List<AccommodationDto> findAll(Pageable pageable) {
        return accommodationService.findAll(pageable);
    }

    @Operation(summary = "Retrieves detailed information about a specific accommodation",
            description = "Retrieves detailed information about a specific accommodation")
    @GetMapping("/{id}")
    public AccommodationDto findAccommodationById(@PathVariable Long id) {
        return accommodationService.findById(id);
    }

    @Operation(summary = "Create new accommodation",
            description = "Create new accommodation")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public AccommodationDto createAccommodation(
            @Valid @RequestBody MergeAccommodationRequestDto mergeAccommodationRequestDto) {
        return accommodationService.createAccommodation(mergeAccommodationRequestDto);
    }

    @Operation(summary = "Update accommodation",
            description = "Update accommodation")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public AccommodationDto updateAccommodation(@PathVariable Long id,
            @Valid @RequestBody MergeAccommodationRequestDto updateAccommodationRequestDto) {
        return accommodationService.updateAccommodation(id, updateAccommodationRequestDto);
    }

    @Operation(summary = "Delete accommodation",
            description = "Delete accommodation")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteAccommodation(@PathVariable Long id) {
        accommodationService.deleteById(id);
    }
}
