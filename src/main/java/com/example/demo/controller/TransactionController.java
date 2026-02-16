package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.dto.*;
import com.example.demo.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;

    /**
     * Create a Transaction table using the request Transaction DTO.
     * @param request consists the body for creation of a table.
     * @return Return a transaction entity using the responseDTO
     */
    @PostMapping
    public ApiResponse<TransactionResponse> create(
            @RequestBody TransactionRequest request) {

        return new ApiResponse<>(
                true,
                service.createTransaction(request)
        );
    }

    /**
     * Used to call the transactions based on the id.
     * @param id used for fetching the table
     * @return returns a transactions based on the id.
     */

    @GetMapping("/{id}")
    public ApiResponse<TransactionResponse> getById(
            @PathVariable String id) {

        return new ApiResponse<>(
                true,
                service.getTransactionById(id)
        );
    }

    /**
     * Give all the transaction details.
     * @return Returns a list of transaction present in the transaction entity.
     */

    @GetMapping
    public ApiResponse<List<TransactionResponse>> getAll() {
        return new ApiResponse<>(
                true,
                service.getAllTransactions()
        );
    }

    /**
     * Gives all transactions based on currency type used for purchasing.
     * @param currency used for fetching transactions based on the currency.
     * @return Api response for returning all the responses based on the currency type.
     */
    @GetMapping("/currency/{currency}")
    public ApiResponse<List<TransactionResponse>> getByCurrency(
            @PathVariable String currency) {

        return new ApiResponse<>(
                true,
                service.getByCurrency(currency)
        );
    }

    /**
     * Gives all the transactions based on the from and to date.
     * @param from consists a localdatetime from which date to check the transactions.
     * @param to consists a localdatetime to which date to check the transactions.
     * @return Returns an ApiResponse based on the from and to date.
     */

    @GetMapping("/sort")
    public ApiResponse<List<TransactionResponse>> getByTime(
             @RequestParam LocalDateTime from , @RequestParam LocalDateTime to) {

        return new ApiResponse<>(
                true,
                service.getByTime(from,to)
        );
    }






}
