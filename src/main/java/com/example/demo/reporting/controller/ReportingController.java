package com.example.demo.reporting.controller;
import com.example.demo.reporting.document.FinancialReport;
import com.example.demo.reporting.service.FinancialReportService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
public class ReportingController {

    private final FinancialReportService service;

    public ReportingController(FinancialReportService service) {
        this.service = service;
    }

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
