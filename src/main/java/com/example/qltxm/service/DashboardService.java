package com.example.qltxm.service;

import com.example.qltxm.dto.RevenuePoint;
import com.example.qltxm.dto.StatusSummary;
import com.example.qltxm.model.BikeStatus;
import com.example.qltxm.model.Rental;
import com.example.qltxm.model.RentalStatus;
import com.example.qltxm.repository.CustomerRepository;
import com.example.qltxm.repository.MotorbikeRepository;
import com.example.qltxm.repository.RentalRepository;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private static final List<RentalStatus> ACTIVE_RENTAL_STATUSES = List.of(RentalStatus.ACTIVE, RentalStatus.APPROVED);
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MM/yyyy", new Locale("vi", "VN"));

    private final MotorbikeRepository motorbikeRepository;
    private final CustomerRepository customerRepository;
    private final RentalRepository rentalRepository;

    public DashboardService(MotorbikeRepository motorbikeRepository,
                            CustomerRepository customerRepository,
                            RentalRepository rentalRepository) {
        this.motorbikeRepository = motorbikeRepository;
        this.customerRepository = customerRepository;
        this.rentalRepository = rentalRepository;
    }

    public long totalBikes() {
        return motorbikeRepository.count();
    }

    public long availableBikes() {
        return motorbikeRepository.countByStatus(BikeStatus.AVAILABLE);
    }

    public long rentedBikes() {
        return motorbikeRepository.countByStatus(BikeStatus.RENTED);
    }

    public long totalCustomers() {
        return customerRepository.count();
    }

    public long activeRentals() {
        return rentalRepository.countByStatusIn(ACTIVE_RENTAL_STATUSES);
    }

    public long pendingRentals() {
        return rentalRepository.countByStatus(RentalStatus.PENDING);
    }

    public List<Rental> recentRentals() {
        return rentalRepository.findTop5ByOrderByStartDateDescIdDesc();
    }

    public double completedRevenue() {
        return rentalRepository.findAll().stream()
                .filter(rental -> rental.getStatus() == RentalStatus.COMPLETED)
                .map(Rental::getFinalAmount)
                .filter(amount -> amount != null)
                .mapToDouble(amount -> amount.doubleValue())
                .sum();
    }

    public List<StatusSummary> rentalStatusSummary() {
        return List.of(
                new StatusSummary("Chờ duyệt", rentalRepository.countByStatus(RentalStatus.PENDING)),
                new StatusSummary("Đang hoạt động", rentalRepository.countByStatusIn(ACTIVE_RENTAL_STATUSES)),
                new StatusSummary("Đã hoàn tất", rentalRepository.countByStatus(RentalStatus.COMPLETED)),
                new StatusSummary("Đã hủy", rentalRepository.countByStatus(RentalStatus.CANCELLED)),
                new StatusSummary("Đã từ chối", rentalRepository.countByStatus(RentalStatus.REJECTED))
        );
    }

    public List<RevenuePoint> monthlyRevenueReport() {
        List<Rental> completedRentals = rentalRepository.findAll().stream()
                .filter(rental -> rental.getStatus() == RentalStatus.COMPLETED)
                .filter(rental -> rental.getActualReturnDate() != null)
                .toList();

        Map<YearMonth, Double> revenueByMonth = completedRentals.stream()
                .collect(Collectors.groupingBy(
                        rental -> YearMonth.from(rental.getActualReturnDate()),
                        Collectors.summingDouble(rental -> rental.getFinalAmount() == null ? 0D : rental.getFinalAmount().doubleValue())
                ));

        YearMonth currentMonth = YearMonth.now();
        List<RevenuePoint> points = new ArrayList<>();
        double max = 0D;
        for (int i = 5; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            double amount = revenueByMonth.getOrDefault(month, 0D);
            max = Math.max(max, amount);
            points.add(new RevenuePoint(month.format(MONTH_FORMAT), amount, 0));
        }

        double safeMax = max <= 0 ? 1D : max;
        return points.stream()
                .map(point -> new RevenuePoint(
                        point.getLabel(),
                        point.getAmount(),
                        (int) Math.max(8, Math.round(point.getAmount() / safeMax * 100))
                ))
                .toList();
    }
}
