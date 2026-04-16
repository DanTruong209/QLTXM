package com.example.qltxm.controller.api;

import com.example.qltxm.dto.api.TrackingResponse;
import com.example.qltxm.model.Motorbike;
import com.example.qltxm.model.Rental;
import com.example.qltxm.service.RentalService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tracking")
public class ApiTrackingController {

    private final RentalService rentalService;

    public ApiTrackingController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    public TrackingResponse tracking(@RequestParam String bookingCode) {
        Rental rental = rentalService.findByBookingCode(bookingCode);
        Motorbike motorbike = rental.getMotorbike();
        return TrackingResponse.from(rental, motorbike.hasLocation()
                ? buildOpenStreetMapEmbedUrl(motorbike.getLatitude(), motorbike.getLongitude())
                : null);
    }

    private String buildOpenStreetMapEmbedUrl(BigDecimal latitude, BigDecimal longitude) {
        BigDecimal offset = new BigDecimal("0.005");
        BigDecimal minLon = longitude.subtract(offset).setScale(6, RoundingMode.HALF_UP);
        BigDecimal minLat = latitude.subtract(offset).setScale(6, RoundingMode.HALF_UP);
        BigDecimal maxLon = longitude.add(offset).setScale(6, RoundingMode.HALF_UP);
        BigDecimal maxLat = latitude.add(offset).setScale(6, RoundingMode.HALF_UP);
        return "https://www.openstreetmap.org/export/embed.html?bbox="
                + minLon.toPlainString() + "%2C" + minLat.toPlainString()
                + "%2C" + maxLon.toPlainString() + "%2C" + maxLat.toPlainString()
                + "&layer=mapnik&marker=" + latitude.toPlainString() + "%2C" + longitude.toPlainString();
    }
}
