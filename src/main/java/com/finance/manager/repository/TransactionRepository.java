package com.finance.manager.repository;

import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    boolean existsByCategory(Category category);

    List<Transaction> findByUserOrderByDateDesc(User user);
    Optional<Transaction> findByIdAndUser(Long id, User user);
}