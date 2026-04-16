package com.example.qltxm.controller;

import com.example.qltxm.model.Motorbike;
import com.example.qltxm.model.Rental;
import com.example.qltxm.service.RentalService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/tracking")
public class TrackingController {

    private final RentalService rentalService;

    public TrackingController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    public String index(@RequestParam(required = false) String bookingCode, Model model) {
        model.addAttribute("bookingCode", bookingCode == null ? "" : bookingCode.trim());
        if (bookingCode == null || bookingCode.isBlank()) {
            return "tracking/index";
        }

        try {
            Rental rental = rentalService.findByBookingCode(bookingCode);
            Motorbike motorbike = rental.getMotorbike();
            model.addAttribute("rental", rental);
            if (motorbike.hasLocation()) {
                model.addAttribute("mapEmbedUrl", buildOpenStreetMapEmbedUrl(motorbike.getLatitude(), motorbike.getLongitude()));
            } else {
                model.addAttribute("locationError", "Xe nay chua duoc cap nhat toa do.");
            }
        } catch (RuntimeException ex) {
            model.addAttribute("lookupError", ex.getMessage());
        }
        return "tracking/index";
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
