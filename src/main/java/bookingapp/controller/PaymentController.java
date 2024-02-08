package bookingapp.controller;

import bookingapp.dto.payment.CreatePaymentResponseDto;
import bookingapp.dto.payment.CreatePaymentResultDto;
import bookingapp.dto.payment.PaymentDto;
import bookingapp.model.Role;
import bookingapp.model.User;
import bookingapp.service.BookingPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@Tag(name = "Managing user booking payment through the platform",
        description = "Managing user booking payment through the platform")
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final BookingPaymentService bookingPaymentService;

    @Operation(summary = "Initiates payment session for booking transaction "
            + "and redirects to payment url",
            description = "Initiates payment session for booking transaction "
                    + "and redirects to payment url")
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public RedirectView createPayment(
            Authentication authentication,
            @PathVariable Long id) {
        Long userId = getUserIdByAuthentication(authentication);
        CreatePaymentResponseDto createPaymentResponseDto =
                bookingPaymentService.createPayment(userId, id);
        return new RedirectView(createPaymentResponseDto.getUrl());
    }

    @Operation(summary = "Handles successful payment processing through Stripe redirection",
            description = "Handles successful payment processing through Stripe redirection")
    @GetMapping("/success/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public CreatePaymentResultDto processSuccessPayment(
            @PathVariable Long id
    ) {
        return bookingPaymentService.processSuccessPayment(id);
    }

    @Operation(summary = "Manages payment cancellation and returns payment paused messages "
            + "during Stripe redirection",
            description = "Manages payment cancellation and returns payment paused messages "
                    + "during Stripe redirection")
    @GetMapping("/cancel/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public CreatePaymentResultDto processCancelPayment(
            @PathVariable Long id
    ) {
        return bookingPaymentService.processCancelPayment(id);
    }

    @Operation(summary = "Get payments list for specific user",
            description = "Get payments list for specific user")
    @GetMapping("/")
    public List<PaymentDto> getPayments(Pageable pageable,
                                        @RequestParam(required = false) Long userId,
                                        Authentication authentication) {
        Long userIdToGetPayments;
        User currentUser = (User) authentication.getPrincipal();
        Set<Role.RoleName> currentUserRoles = currentUser.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        if (currentUserRoles.contains(Role.RoleName.ADMIN)) {
            userIdToGetPayments = userId;
        } else if (currentUserRoles.contains(Role.RoleName.CUSTOMER)) {
            userIdToGetPayments = currentUser.getId();
        } else {
            return Collections.emptyList();
        }
        return bookingPaymentService.getPayments(pageable, userIdToGetPayments);
    }

    private Long getUserIdByAuthentication(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }
}
