package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.dto.*;
import com.example.demo.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;



import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.TransactionResponse;
import com.example.demo.dto.RefundRequest;
import com.example.demo.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/refund")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService service;

    /**
     * Create a refund transaction
     * @param request Contains the body of the refund request.
     * @return Api response with the successfull refund message.
     */
    @PostMapping
    public ApiResponse<TransactionResponse> refund(
            @RequestBody RefundRequest request) {

        return new ApiResponse<>(
                true,
                service.refund(request)
        );
    }

}