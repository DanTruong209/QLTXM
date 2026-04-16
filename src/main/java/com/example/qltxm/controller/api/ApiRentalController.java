package com.example.qltxm.controller.api;

import com.example.qltxm.dto.api.RentalCompleteRequest;
import com.example.qltxm.dto.api.RentalRejectRequest;
import com.example.qltxm.dto.api.RentalRequest;
import com.example.qltxm.dto.api.RentalResponse;
import com.example.qltxm.model.Customer;
import com.example.qltxm.model.Motorbike;
import com.example.qltxm.model.Rental;
import com.example.qltxm.model.RentalStatus;
import com.example.qltxm.service.RentalService;
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
@RequestMapping("/api/rentals")
public class ApiRentalController {

    private final RentalService rentalService;

    public ApiRentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    public List<RentalResponse> list(@RequestParam(required = false) RentalStatus status,
                                     @RequestParam(required = false) String q) {
        return rentalService.search(q, status).stream()
                .map(RentalResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public RentalResponse detail(@PathVariable Long id) {
        return RentalResponse.from(rentalService.findById(id));
    }

    @PostMapping
    public ResponseEntity<RentalResponse> create(@Valid @RequestBody RentalRequest request) {
        Rental saved = rentalService.create(toRental(request));
        return ResponseEntity.created(URI.create("/api/rentals/" + saved.getId()))
                .body(RentalResponse.from(saved));
    }

    @PutMapping("/{id}")
    public RentalResponse update(@PathVariable Long id, @Valid @RequestBody RentalRequest request) {
        return RentalResponse.from(rentalService.update(id, toRental(request)));
    }

    @PostMapping("/{id}/approve")
    public RentalResponse approve(@PathVariable Long id) {
        rentalService.approve(id);
        return RentalResponse.from(rentalService.findById(id));
    }

    @PostMapping("/{id}/complete")
    public RentalResponse complete(@PathVariable Long id, @Valid @RequestBody RentalCompleteRequest request) {
        rentalService.complete(id, request.getActualReturnDate(), request.getExtraFee(), request.getReturnNotes());
        return RentalResponse.from(rentalService.findById(id));
    }

    @PostMapping("/{id}/cancel")
    public RentalResponse cancel(@PathVariable Long id) {
        rentalService.cancel(id);
        return RentalResponse.from(rentalService.findById(id));
    }

    @PostMapping("/{id}/reject")
    public RentalResponse reject(@PathVariable Long id, @RequestBody(required = false) RentalRejectRequest request) {
        rentalService.reject(id, request == null ? null : request.getReason());
        return RentalResponse.from(rentalService.findById(id));
    }

    private Rental toRental(RentalRequest request) {
        Rental rental = new Rental();
        Customer customer = new Customer();
        customer.setId(request.getCustomerId());
        Motorbike motorbike = new Motorbike();
        motorbike.setId(request.getMotorbikeId());
        rental.setCustomer(customer);
        rental.setMotorbike(motorbike);
        rental.setStartDate(request.getStartDate());
        rental.setEndDate(request.getEndDate());
        rental.setDepositAmount(request.getDepositAmount());
        rental.setNotes(request.getNotes());
        return rental;
    }
}
