package com.example.qltxm;

import com.example.qltxm.service.DashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final DashboardService dashboardService;

    public HomeController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String index(Model model, Authentication authentication) {
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            return "redirect:/user";
        }
        model.addAttribute("totalBikes", dashboardService.totalBikes());
        model.addAttribute("availableBikes", dashboardService.availableBikes());
        model.addAttribute("rentedBikes", dashboardService.rentedBikes());
        model.addAttribute("totalCustomers", dashboardService.totalCustomers());
        model.addAttribute("activeRentals", dashboardService.activeRentals());
        model.addAttribute("pendingRentals", dashboardService.pendingRentals());
        model.addAttribute("completedRevenue", dashboardService.completedRevenue());
        model.addAttribute("recentRentals", dashboardService.recentRentals());
        model.addAttribute("statusSummary", dashboardService.rentalStatusSummary());
        model.addAttribute("monthlyRevenue", dashboardService.monthlyRevenueReport());
        return "index";
    }
}
