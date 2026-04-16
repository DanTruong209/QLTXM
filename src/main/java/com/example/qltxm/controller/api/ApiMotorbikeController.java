package com.example.qltxm.controller.api;

import com.example.qltxm.dto.api.MotorbikeRequest;
import com.example.qltxm.dto.api.MotorbikeResponse;
import com.example.qltxm.model.BikeStatus;
import com.example.qltxm.model.Motorbike;
import com.example.qltxm.repository.MotorbikeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/motorbikes")
public class ApiMotorbikeController {

    private final MotorbikeRepository motorbikeRepository;

    public ApiMotorbikeController(MotorbikeRepository motorbikeRepository) {
        this.motorbikeRepository = motorbikeRepository;
    }

    @GetMapping
    public List<MotorbikeResponse> list(@RequestParam(required = false) String q,
                                        @RequestParam(required = false) BikeStatus status) {
        return motorbikeRepository.search(normalizeKeyword(q), status).stream()
                .map(MotorbikeResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public MotorbikeResponse detail(@PathVariable Long id) {
        return MotorbikeResponse.from(findMotorbike(id));
    }

    @PostMapping
    public ResponseEntity<MotorbikeResponse> create(@Valid @RequestBody MotorbikeRequest request) {
        Motorbike motorbike = new Motorbike();
        apply(motorbike, request);
        Motorbike saved = motorbikeRepository.save(motorbike);
        return ResponseEntity.created(URI.create("/api/motorbikes/" + saved.getId()))
                .body(MotorbikeResponse.from(saved));
    }

    @PutMapping("/{id}")
    public MotorbikeResponse update(@PathVariable Long id, @Valid @RequestBody MotorbikeRequest request) {
        Motorbike motorbike = findMotorbike(id);
        apply(motorbike, request);
        return MotorbikeResponse.from(motorbikeRepository.save(motorbike));
    }

    private Motorbike findMotorbike(Long id) {
        return motorbikeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Khong tim thay xe may"));
    }

    private void apply(Motorbike motorbike, MotorbikeRequest request) {
        motorbike.setCode(request.getCode().trim());
        motorbike.setBrand(request.getBrand().trim());
        motorbike.setModel(request.getModel().trim());
        motorbike.setLicensePlate(request.getLicensePlate().trim());
        motorbike.setDailyRate(request.getDailyRate());
        motorbike.setStatus(request.getStatus());
        motorbike.setNotes(trimToNull(request.getNotes()));
        motorbike.setImageUrl(trimToNull(request.getImageUrl()));
        motorbike.setLatitude(request.getLatitude());
        motorbike.setLongitude(request.getLongitude());
        motorbike.setLocationLabel(trimToNull(request.getLocationLabel()));
    }

    private String normalizeKeyword(String keyword) {
        return keyword == null || keyword.isBlank() ? null : keyword.trim();
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
