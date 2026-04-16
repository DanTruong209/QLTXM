package com.example.qltxm.dto.api;

import com.example.qltxm.model.Motorbike;
import java.math.BigDecimal;

public record MotorbikeResponse(
        Long id,
        String code,
        String brand,
        String model,
        String licensePlate,
        BigDecimal dailyRate,
        String status,
        String notes,
        String imageUrl,
        BigDecimal latitude,
        BigDecimal longitude,
        String locationLabel
) {

    public static MotorbikeResponse from(Motorbike motorbike) {
        return new MotorbikeResponse(
                motorbike.getId(),
                motorbike.getCode(),
                motorbike.getBrand(),
                motorbike.getModel(),
                motorbike.getLicensePlate(),
                motorbike.getDailyRate(),
                motorbike.getStatus().name(),
                motorbike.getNotes(),
                motorbike.getImageUrl(),
                motorbike.getLatitude(),
                motorbike.getLongitude(),
                motorbike.getLocationLabel()
        );
    }
}
