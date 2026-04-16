package com.example.qltxm.dto.api;

import com.example.qltxm.model.Motorbike;
import com.example.qltxm.model.Rental;

public record TrackingResponse(
        String bookingCode,
        String rentalStatus,
        String customerName,
        MotorbikeResponse motorbike,
        String locationLabel,
        String mapEmbedUrl
) {

    public static TrackingResponse from(Rental rental, String mapEmbedUrl) {
        Motorbike motorbike = rental.getMotorbike();
        return new TrackingResponse(
                rental.getBookingCode(),
                rental.getStatus().name(),
                rental.getCustomer().getFullName(),
                MotorbikeResponse.from(motorbike),
                motorbike.getLocationLabel(),
                mapEmbedUrl
        );
    }
}
