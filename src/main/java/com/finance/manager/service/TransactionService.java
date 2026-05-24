package com.finance.manager.service;

import com.finance.manager.dto.TransactionRequest;
import com.finance.manager.dto.TransactionResponse;
import com.finance.manager.dto.TransactionUpdateRequest;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.repository.CategoryRepository;
import com.finance.manager.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    public TransactionResponse addTransaction(TransactionRequest request, User user) {

        Category category = categoryRepository.findByNameAndUser(request.getCategoryName(), user)
                .orElseGet(() -> categoryRepository.findByNameAndUserIsNull(request.getCategoryName())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid category name provided")));

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setDate(request.getDate());
        transaction.setDescription(request.getDescription());
        transaction.setCategory(category);
        transaction.setUser(user); 

        Transaction saved = transactionRepository.save(transaction);

        return new TransactionResponse(
                saved.getId(),
                saved.getAmount(),
                saved.getDate(),
                saved.getDescription(),
                saved.getCategory().getName(),
                saved.getCategory().getType());
    }


    public List<TransactionResponse> getAllTransactions(User user, LocalDate startDate, LocalDate endDate, Long categoryId) {
        List<Transaction> allTransactions = transactionRepository.findByUserOrderByDateDesc(user);
        
        List<TransactionResponse> responses = new ArrayList<>();

        for (Transaction t : allTransactions) {
            if (startDate != null && t.getDate().isBefore(startDate)) continue;
            
            if (endDate != null && t.getDate().isAfter(endDate)) continue;
            
            if (categoryId != null && !t.getCategory().getId().equals(categoryId)) continue;

            responses.add(new TransactionResponse(
                    t.getId(), t.getAmount(), t.getDate(), t.getDescription(),
                    t.getCategory().getName(), t.getCategory().getType()
            ));
        }

        return responses;
    }

    public TransactionResponse updateTransaction(Long id, TransactionUpdateRequest request, User user) {
        Transaction transaction = transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }
        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }
        if (request.getCategoryName() != null) {
            Category category = categoryRepository.findByNameAndUser(request.getCategoryName(), user)
                    .orElseGet(() -> categoryRepository.findByNameAndUserIsNull(request.getCategoryName())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid category name provided")));
            transaction.setCategory(category);
        }

        @SuppressWarnings("null")
        Transaction updated = transactionRepository.save(transaction);

        return new TransactionResponse(
                updated.getId(),
                updated.getAmount(),
                updated.getDate(),
                updated.getDescription(),
                updated.getCategory().getName(),
                updated.getCategory().getType()
        );
    }

    public void deleteTransaction(Long id, User user) {
        Transaction transaction = transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transactionRepository.delete(transaction);
    }
}