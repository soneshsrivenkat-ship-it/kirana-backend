package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.dto.*;
import com.example.demo.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;

    /**
     * Get the summary of the day sales.
     * @param currency Used to get the summary of sales in any currency
     * @return A Api Response for total sales per day.
     */

    @GetMapping("/summary")
    public ApiResponse<SalesSummaryResponse> getSalesSummary(
            @RequestParam(defaultValue = "INR") String currency) {

        SalesSummaryResponse response =
                salesService.getSalesSummary(currency);

        return new ApiResponse<>(true, response);
    }
}
