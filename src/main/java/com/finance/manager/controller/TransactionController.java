package com.finance.manager.controller;

import com.finance.manager.entity.User;
import com.finance.manager.dto.TransactionListResponse;
import com.finance.manager.dto.TransactionRequest;
import com.finance.manager.dto.TransactionResponse;
import com.finance.manager.dto.TransactionUpdateRequest;
import com.finance.manager.service.TransactionService;
import com.finance.manager.repository.UserRepository;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserRepository userRepository;

    public TransactionController(TransactionService transactionService, UserRepository userRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public ResponseEntity<TransactionListResponse> getAllTransactions(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(value = "categoryId", required = false) Long categoryId) { 

        User user = getAuthenticatedUser();

        List<TransactionResponse> transactions = transactionService.getAllTransactions(user, startDate, endDate,
                categoryId);

        return ResponseEntity.ok(new TransactionListResponse(transactions));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> addTransaction(@Valid @RequestBody TransactionRequest request) {
        User user = getAuthenticatedUser();
        TransactionResponse response = transactionService.addTransaction(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id, 
            @Valid @RequestBody TransactionUpdateRequest request) {
        
        User user = getAuthenticatedUser();
        TransactionResponse response = transactionService.updateTransaction(id, request, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        User user = getAuthenticatedUser();
        transactionService.deleteTransaction(id, user);
        return ResponseEntity.noContent().build();
    }
}
