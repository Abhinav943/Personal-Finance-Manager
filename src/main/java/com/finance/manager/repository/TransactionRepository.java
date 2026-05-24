package com.finance.manager.repository;

import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserOrderByDateDesc(User user);

    boolean existsByCategory(Category category);
}