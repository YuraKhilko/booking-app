package bookingapp.controller;

import bookingapp.dto.booking.BookingDto;
import bookingapp.dto.booking.CreateBookingRequestDto;
import bookingapp.dto.booking.UpdateBookingRequestDto;
import bookingapp.model.Booking;
import bookingapp.model.User;
import bookingapp.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Managing users' bookings",
        description = "Managing users' bookings")
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @Operation(summary = "Retrieves detailed information about a specific booking",
            description = "Retrieves detailed information about a specific booking")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public BookingDto findBookingById(
            Authentication authentication,
            @PathVariable Long id) {
        Long userId = getUserIdByAuthentication(authentication);
        return bookingService.findById(userId, id);
    }

    @Operation(summary = "Create new booking",
            description = "Create new booking")
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public BookingDto createBooking(
            Authentication authentication,
            @Valid @RequestBody CreateBookingRequestDto createBookingRequestDto) {
        Long userId = getUserIdByAuthentication(authentication);
        return bookingService.createBooking(userId, createBookingRequestDto);
    }

    @Operation(summary = "Get customer's list of bookings",
            description = "Get customer's list of bookings")
    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<BookingDto> getBookings(
            Authentication authentication) {
        Long userId = getUserIdByAuthentication(authentication);
        return bookingService.getBookings(userId);
    }

    @Operation(summary = "Get customer's list of bookings and filter by userId, status",
            description = "Get customer's list of bookings and filter by userId, status")
    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public List<BookingDto> getBookings(Pageable pageable,
                                        @RequestParam(required = false) Long userId,
                                        @RequestParam(required = false) Booking.Status status) {
        return bookingService.getBookings(pageable, userId, status);
    }

    @Operation(summary = "Update customer booking",
            description = "Update customer booking")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public BookingDto updateBooking(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookingRequestDto updateBookingRequestDto) {
        Long userId = getUserIdByAuthentication(authentication);
        return bookingService.updateBooking(userId, id, updateBookingRequestDto);
    }

    @Operation(summary = "Delete customer booking",
            description = "Delete customer booking")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteBooking(
            Authentication authentication,
            @PathVariable Long id) {
        Long userId = getUserIdByAuthentication(authentication);
        bookingService.deleteById(userId, id);
    }

    private Long getUserIdByAuthentication(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }
}
