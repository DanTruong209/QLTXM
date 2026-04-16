package com.example.qltxm.controller.api;

import com.example.qltxm.dto.api.CustomerRequest;
import com.example.qltxm.dto.api.CustomerResponse;
import com.example.qltxm.model.Customer;
import com.example.qltxm.repository.CustomerRepository;
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
@RequestMapping("/api/customers")
public class ApiCustomerController {

    private final CustomerRepository customerRepository;

    public ApiCustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public List<CustomerResponse> list(@RequestParam(required = false) String q) {
        return customerRepository.search(normalizeKeyword(q)).stream()
                .map(CustomerResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public CustomerResponse detail(@PathVariable Long id) {
        return CustomerResponse.from(findCustomer(id));
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        validateUniqueFields(request, null);
        Customer customer = new Customer();
        apply(customer, request);
        Customer saved = customerRepository.save(customer);
        return ResponseEntity.created(URI.create("/api/customers/" + saved.getId()))
                .body(CustomerResponse.from(saved));
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        validateUniqueFields(request, id);
        Customer customer = findCustomer(id);
        apply(customer, request);
        return CustomerResponse.from(customerRepository.save(customer));
    }

    private Customer findCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Khong tim thay khach hang"));
    }

    private void validateUniqueFields(CustomerRequest request, Long id) {
        String phone = request.getPhone().trim();
        String idCard = request.getIdCard().trim();
        boolean phoneExists = id == null
                ? customerRepository.existsByPhone(phone)
                : customerRepository.existsByPhoneAndIdNot(phone, id);
        if (phoneExists) {
            throw new IllegalArgumentException("So dien thoai da duoc su dung");
        }
        boolean idCardExists = id == null
                ? customerRepository.existsByIdCard(idCard)
                : customerRepository.existsByIdCardAndIdNot(idCard, id);
        if (idCardExists) {
            throw new IllegalArgumentException("CCCD/CMND da duoc su dung");
        }
    }

    private void apply(Customer customer, CustomerRequest request) {
        customer.setFullName(request.getFullName().trim());
        customer.setPhone(request.getPhone().trim());
        customer.setIdCard(request.getIdCard().trim());
        customer.setAddress(trimToNull(request.getAddress()));
        customer.setNotes(trimToNull(request.getNotes()));
    }

    private String normalizeKeyword(String keyword) {
        return keyword == null || keyword.isBlank() ? null : keyword.trim();
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
