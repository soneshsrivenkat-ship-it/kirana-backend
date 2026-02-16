package com.example.demo.reporting.controller;

import com.example.demo.reporting.document.FinancialReport;
import com.example.demo.reporting.service.FinancialReportService;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for fetching financial reports.
 *
 * Provides endpoints to retrieve:
 *  - Weekly reports
 *  - Monthly reports
 *  - Yearly reports
 */
@RestController
@RequestMapping("/reports")
public class ReportingController {

    private final FinancialReportService service;

    public ReportingController(FinancialReportService service) {
        this.service = service;
    }

    /**
     * Fetch weekly financial report.
     */
    @GetMapping("/weekly")
    public FinancialReport getWeeklyReport(
            @RequestParam String userId,
            @RequestParam String week,
            @RequestParam String currency
    ) {
        return service
                .findByUserIdAndPeriodTypeAndPeriodAndCurrencyType(
                        userId, "WEEKLY", week, currency
                )
                .orElseThrow();
    }

    /**
     * Fetch monthly financial report.
     */
    @GetMapping("/monthly")
    public FinancialReport getMonthlyReport(
            @RequestParam String userId,
            @RequestParam String month,
            @RequestParam String currency
    ) {
        return service
                .findByUserIdAndPeriodTypeAndPeriodAndCurrencyType(
                        userId, "MONTHLY", month, currency
                )
                .orElseThrow();
    }

    /**
     * Fetch yearly financial report.
     */
    @GetMapping("/yearly")
    public FinancialReport getYearlyReport(
            @RequestParam String userId,
            @RequestParam String year,
            @RequestParam String currency
    ) {
        return service
                .findByUserIdAndPeriodTypeAndPeriodAndCurrencyType(
                        userId, "YEARLY", year, currency
                )
                .orElseThrow();
    }
}
