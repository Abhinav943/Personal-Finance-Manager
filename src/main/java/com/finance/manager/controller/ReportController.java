package com.finance.manager.controller;

import com.finance.manager.dto.MonthlyReportResponse;
import com.finance.manager.dto.YearlyReportResponse;
import com.finance.manager.entity.User;
import com.finance.manager.repository.UserRepository;
import com.finance.manager.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final UserRepository userRepository;

    public ReportController(ReportService reportService, UserRepository userRepository) {
        this.reportService = reportService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport(
            @PathVariable int year,
            @PathVariable int month) {

        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Invalid month. Must be between 1 and 12.");
        }

        User user = getAuthenticatedUser();
        MonthlyReportResponse response = reportService.getMonthlyReport(user, year, month);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/yearly/{year}")
    public ResponseEntity<YearlyReportResponse> getYearlyReport(
            @PathVariable int year) {
        User user = getAuthenticatedUser();
        YearlyReportResponse response = reportService.getYearlyReport(user, year);
        return ResponseEntity.ok(response);
    }
}