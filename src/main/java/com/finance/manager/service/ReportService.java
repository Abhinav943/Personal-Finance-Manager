package com.finance.manager.service;

import com.finance.manager.dto.MonthlyReportResponse;
import com.finance.manager.dto.YearlyReportResponse;
import com.finance.manager.entity.CategoryType;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final TransactionRepository transactionRepository;

    public ReportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public MonthlyReportResponse getMonthlyReport(User user, int year, int month) {
        List<Transaction> transactions = transactionRepository.findByUserOrderByDateDesc(user);

        Map<String, BigDecimal> incomeByCategory = new HashMap<>();
        Map<String, BigDecimal> expensesByCategory = new HashMap<>();
        BigDecimal totalIncomeSum = BigDecimal.ZERO;
        BigDecimal totalExpenseSum = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            if (t.getDate().getYear() == year && t.getDate().getMonthValue() == month) {
                String categoryName = t.getCategory().getName();
                BigDecimal amount = t.getAmount();

                if (t.getCategory().getType() == CategoryType.INCOME) {
                    incomeByCategory.put(categoryName,
                            incomeByCategory.getOrDefault(categoryName, BigDecimal.ZERO).add(amount));
                    totalIncomeSum = totalIncomeSum.add(amount);
                } else if (t.getCategory().getType() == CategoryType.EXPENSE) {
                    expensesByCategory.put(categoryName,
                            expensesByCategory.getOrDefault(categoryName, BigDecimal.ZERO).add(amount));
                    totalExpenseSum = totalExpenseSum.add(amount);
                }
            }
        }

        BigDecimal netSavings = totalIncomeSum.subtract(totalExpenseSum);

        return new MonthlyReportResponse(month, year, incomeByCategory, expensesByCategory, netSavings);
    }

    public YearlyReportResponse getYearlyReport(User user, int year) {
        List<Transaction> transactions = transactionRepository.findByUserOrderByDateDesc(user);

        Map<String, BigDecimal> incomeByCategory = new HashMap<>();
        Map<String, BigDecimal> expensesByCategory = new HashMap<>();
        BigDecimal totalIncomeSum = BigDecimal.ZERO;
        BigDecimal totalExpenseSum = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            if (t.getDate().getYear() == year) {
                String categoryName = t.getCategory().getName();
                BigDecimal amount = t.getAmount();

                if (t.getCategory().getType() == CategoryType.INCOME) {
                    incomeByCategory.put(categoryName,
                            incomeByCategory.getOrDefault(categoryName, BigDecimal.ZERO).add(amount));
                    totalIncomeSum = totalIncomeSum.add(amount);
                } else if (t.getCategory().getType() == CategoryType.EXPENSE) {
                    expensesByCategory.put(categoryName,
                            expensesByCategory.getOrDefault(categoryName, BigDecimal.ZERO).add(amount));
                    totalExpenseSum = totalExpenseSum.add(amount);
                }
            }
        }

        BigDecimal netSavings = totalIncomeSum.subtract(totalExpenseSum);

        return new YearlyReportResponse(year, incomeByCategory, expensesByCategory, netSavings);
    }
}