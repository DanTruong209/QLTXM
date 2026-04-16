package com.example.qltxm.dto.api;

import com.example.qltxm.model.Rental;
import java.math.BigDecimal;
import java.time.LocalDate;

public record RentalResponse(
        Long id,
        String bookingCode,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate actualReturnDate,
        BigDecimal dailyRate,
        BigDecimal depositAmount,
        BigDecimal totalAmount,
        BigDecimal lateFee,
        BigDecimal finalAmount,
        String paymentMethod,
        String paymentStatus,
        BigDecimal paidAmount,
        String status,
        String notes,
        String returnNotes,
        CustomerSummary customer,
        MotorbikeSummary motorbike
) {

    public static RentalResponse from(Rental rental) {
        return new RentalResponse(
                rental.getId(),
                rental.getBookingCode(),
                rental.getStartDate(),
                rental.getEndDate(),
                rental.getActualReturnDate(),
                rental.getDailyRate(),
                rental.getDepositAmount(),
                rental.getTotalAmount(),
                rental.getLateFee(),
                rental.getFinalAmount(),
                rental.getPaymentMethod() == null ? null : rental.getPaymentMethod().name(),
                rental.getPaymentStatus() == null ? null : rental.getPaymentStatus().name(),
                rental.getPaidAmount(),
                rental.getStatus().name(),
                rental.getNotes(),
                rental.getReturnNotes(),
                new CustomerSummary(
                        rental.getCustomer().getId(),
                        rental.getCustomer().getFullName(),
                        rental.getCustomer().getPhone()
                ),
                new MotorbikeSummary(
                        rental.getMotorbike().getId(),
                        rental.getMotorbike().getCode(),
                        rental.getMotorbike().getBrand(),
                        rental.getMotorbike().getModel(),
                        rental.getMotorbike().getLicensePlate(),
                        rental.getMotorbike().getStatus().name()
                )
        );
    }

    public record CustomerSummary(Long id, String fullName, String phone) {
    }

    public record MotorbikeSummary(
            Long id,
            String code,
            String brand,
            String model,
            String licensePlate,
            String status
    ) {
    }
}
