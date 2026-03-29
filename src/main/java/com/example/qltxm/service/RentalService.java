package com.example.qltxm.service;

import com.example.qltxm.dto.RentalCheckoutSummary;
import com.example.qltxm.dto.UserPaymentForm;
import com.example.qltxm.dto.UserRentalForm;
import com.example.qltxm.model.BikeStatus;
import com.example.qltxm.model.Customer;
import com.example.qltxm.model.Motorbike;
import com.example.qltxm.model.PaymentStatus;
import com.example.qltxm.model.Rental;
import com.example.qltxm.model.RentalStatus;
import com.example.qltxm.repository.CustomerRepository;
import com.example.qltxm.repository.MotorbikeRepository;
import com.example.qltxm.repository.RentalRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RentalService {

    private static final List<RentalStatus> ACTIVE_RENTAL_STATUSES = List.of(RentalStatus.ACTIVE, RentalStatus.APPROVED);

    private final RentalRepository rentalRepository;
    private final MotorbikeRepository motorbikeRepository;
    private final CustomerRepository customerRepository;

    public RentalService(RentalRepository rentalRepository,
                         MotorbikeRepository motorbikeRepository,
                         CustomerRepository customerRepository) {
        this.rentalRepository = rentalRepository;
        this.motorbikeRepository = motorbikeRepository;
        this.customerRepository = customerRepository;
    }

    public List<Rental> findAll() {
        return rentalRepository.findAllByOrderByStartDateDescIdDesc();
    }

    public List<Rental> search(String keyword, RentalStatus status) {
        return rentalRepository.search(status, normalizeKeyword(keyword));
    }

    public List<Rental> findAllByStatus(RentalStatus status) {
        return rentalRepository.findAllByStatusOrderByStartDateDescIdDesc(status);
    }

    public List<Rental> findAllByCustomerId(Long customerId) {
        return rentalRepository.findAllByCustomerIdOrderByStartDateDescIdDesc(customerId);
    }

    public long countActiveByCustomerId(Long customerId) {
        return rentalRepository.findAllByCustomerIdOrderByStartDateDescIdDesc(customerId).stream()
                .filter(rental -> ACTIVE_RENTAL_STATUSES.contains(rental.getStatus()))
                .count();
    }

    public Rental findById(Long id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Khong tim thay phieu thue"));
    }

    public RentalCheckoutSummary prepareCheckout(UserRentalForm form) {
        Motorbike bike = findAvailableBike(form.getMotorbikeId());
        validateDates(form.getStartDate(), form.getEndDate());
        BigDecimal depositAmount = defaultMoney(form.getDepositAmount());
        long rentalDays = ChronoUnit.DAYS.between(form.getStartDate(), form.getEndDate()) + 1;
        BigDecimal rentalTotal = bike.getDailyRate().multiply(BigDecimal.valueOf(rentalDays));
        BigDecimal remainingAmount = rentalTotal.subtract(depositAmount).max(BigDecimal.ZERO);
        return new RentalCheckoutSummary(
                bike,
                rentalDays,
                bike.getDailyRate(),
                rentalTotal,
                depositAmount,
                remainingAmount
        );
    }

    @Transactional
    public Rental create(Rental rental) {
        validateRental(rental);
        Motorbike bike = motorbikeRepository.findById(rental.getMotorbike().getId())
                .orElseThrow(() -> new EntityNotFoundException("Khong tim thay xe"));
        Customer customer = customerRepository.findById(rental.getCustomer().getId())
                .orElseThrow(() -> new EntityNotFoundException("Khong tim thay khach hang"));
        rental.setMotorbike(bike);
        rental.setCustomer(customer);
        rental.setDailyRate(bike.getDailyRate());
        rental.setStatus(RentalStatus.PENDING);
        rental.setLateFee(BigDecimal.ZERO);
        rental.setFinalAmount(rental.getTotalAmount());
        if (rental.getPaymentStatus() == null) {
            rental.setPaymentStatus(PaymentStatus.UNPAID);
        }
        if (rental.getPaidAmount() == null) {
            rental.setPaidAmount(BigDecimal.ZERO);
        }
        return rentalRepository.save(rental);
    }

    @Transactional
    public Rental update(Long id, Rental rentalData) {
        Rental rental = findById(id);
        if (rental.getStatus() != RentalStatus.PENDING) {
            throw new IllegalStateException("Chi duoc sua phieu thue dang cho duyet");
        }
        validateRental(rentalData);
        Customer customer = customerRepository.findById(rentalData.getCustomer().getId())
                .orElseThrow(() -> new EntityNotFoundException("Khong tim thay khach hang"));
        rental.setCustomer(customer);
        rental.setStartDate(rentalData.getStartDate());
        rental.setEndDate(rentalData.getEndDate());
        rental.setDepositAmount(defaultMoney(rentalData.getDepositAmount()));
        rental.setNotes(rentalData.getNotes());
        return rentalRepository.save(rental);
    }

    @Transactional
    public void approve(Long id) {
        Rental rental = findById(id);
        if (rental.getStatus() != RentalStatus.PENDING) {
            throw new IllegalStateException("Chi duyet duoc phieu dang cho duyet");
        }
        Motorbike bike = rental.getMotorbike();
        if (bike.getStatus() != BikeStatus.AVAILABLE) {
            throw new IllegalStateException("Xe hien khong san sang de duyet cho thue");
        }
        bike.setStatus(BikeStatus.RENTED);
        rental.setStatus(RentalStatus.APPROVED);
        rental.setFinalAmount(rental.getTotalAmount());
        motorbikeRepository.save(bike);
        rentalRepository.save(rental);
    }

    @Transactional
    public void complete(Long id, LocalDate actualReturnDate, BigDecimal extraFee, String returnNotes) {
        Rental rental = findById(id);
        if (!ACTIVE_RENTAL_STATUSES.contains(rental.getStatus())) {
            throw new IllegalStateException("Chi tra xe cho phieu dang hoat dong");
        }
        if (actualReturnDate == null) {
            throw new IllegalArgumentException("Vui long chon ngay tra thuc te");
        }
        if (actualReturnDate.isBefore(rental.getStartDate())) {
            throw new IllegalArgumentException("Ngay tra thuc te khong duoc nho hon ngay thue");
        }

        rental.setStatus(RentalStatus.COMPLETED);
        rental.setActualReturnDate(actualReturnDate);
        BigDecimal lateFee = calculateLateFee(rental, actualReturnDate);
        BigDecimal extra = defaultMoney(extraFee);
        rental.setLateFee(lateFee.add(extra));
        rental.setReturnNotes(returnNotes);
        rental.setFinalAmount(rental.getTotalAmount().add(rental.getLateFee()));

        Motorbike bike = rental.getMotorbike();
        bike.setStatus(BikeStatus.AVAILABLE);
        motorbikeRepository.save(bike);
        rentalRepository.save(rental);
    }

    @Transactional
    public void cancel(Long id) {
        Rental rental = findById(id);
        RentalStatus previousStatus = rental.getStatus();
        if (previousStatus == RentalStatus.COMPLETED || previousStatus == RentalStatus.REJECTED || previousStatus == RentalStatus.CANCELLED) {
            throw new IllegalStateException("Phieu thue nay khong the huy");
        }
        rental.setStatus(RentalStatus.CANCELLED);
        if (ACTIVE_RENTAL_STATUSES.contains(previousStatus) || rental.getMotorbike().getStatus() == BikeStatus.RENTED) {
            Motorbike bike = rental.getMotorbike();
            bike.setStatus(BikeStatus.AVAILABLE);
            motorbikeRepository.save(bike);
        }
        rentalRepository.save(rental);
    }

    @Transactional
    public void reject(Long id, String note) {
        Rental rental = findById(id);
        if (rental.getStatus() != RentalStatus.PENDING) {
            throw new IllegalStateException("Chi tu choi duoc phieu dang cho duyet");
        }
        rental.setStatus(RentalStatus.REJECTED);
        rental.setReturnNotes(note == null || note.isBlank()
                ? "Don thue khong phu hop dieu kien tiep nhan"
                : note.trim());
        rentalRepository.save(rental);
    }

    @Transactional
    public Rental createForCustomer(Long customerId, UserRentalForm form) {
        Rental rental = new Rental();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Khong tim thay khach hang"));
        Motorbike bike = motorbikeRepository.findById(form.getMotorbikeId())
                .orElseThrow(() -> new EntityNotFoundException("Khong tim thay xe"));
        rental.setCustomer(customer);
        rental.setMotorbike(bike);
        rental.setStartDate(form.getStartDate());
        rental.setEndDate(form.getEndDate());
        rental.setDepositAmount(defaultMoney(form.getDepositAmount()));
        rental.setNotes(form.getNotes());
        return create(rental);
    }

    @Transactional
    public Rental createForCustomer(Long customerId, UserPaymentForm form) {
        Rental rental = new Rental();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Khong tim thay khach hang"));
        Motorbike bike = motorbikeRepository.findById(form.getMotorbikeId())
                .orElseThrow(() -> new EntityNotFoundException("Khong tim thay xe"));
        rental.setCustomer(customer);
        rental.setMotorbike(bike);
        rental.setStartDate(form.getStartDate());
        rental.setEndDate(form.getEndDate());
        rental.setDepositAmount(defaultMoney(form.getDepositAmount()));
        rental.setNotes(form.getNotes());
        rental.setPaymentMethod(form.getPaymentMethod());
        rental.setPaymentStatus(PaymentStatus.DEPOSIT_PAID);
        rental.setPaidAmount(defaultMoney(form.getDepositAmount()));
        return create(rental);
    }

    private void validateRental(Rental rental) {
        if (rental.getCustomer() == null || rental.getCustomer().getId() == null) {
            throw new IllegalArgumentException("Vui long chon khach hang");
        }
        if (rental.getMotorbike() == null || rental.getMotorbike().getId() == null) {
            throw new IllegalArgumentException("Vui long chon xe may");
        }
        validateDates(rental.getStartDate(), rental.getEndDate());
        findAvailableBike(rental.getMotorbike().getId());
    }

    private BigDecimal calculateLateFee(Rental rental, LocalDate actualReturnDate) {
        if (actualReturnDate == null || rental.getEndDate() == null || !actualReturnDate.isAfter(rental.getEndDate())) {
            return BigDecimal.ZERO;
        }
        long lateDays = ChronoUnit.DAYS.between(rental.getEndDate(), actualReturnDate);
        return rental.getDailyRate()
                .multiply(BigDecimal.valueOf(lateDays))
                .multiply(BigDecimal.valueOf(0.3));
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Vui long chon ngay thue va ngay tra du kien");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Ngay thue khong duoc o trong qua khu");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Ngay tra du kien phai lon hon hoac bang ngay thue");
        }
    }

    private Motorbike findAvailableBike(Long motorbikeId) {
        Motorbike bike = motorbikeRepository.findById(motorbikeId)
                .orElseThrow(() -> new EntityNotFoundException("Khong tim thay xe"));
        if (bike.getStatus() != BikeStatus.AVAILABLE) {
            throw new IllegalStateException("Xe dang khong o trang thai san sang");
        }
        return bike;
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return keyword.trim();
    }
}
