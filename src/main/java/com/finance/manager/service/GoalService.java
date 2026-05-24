package com.finance.manager.service;

import com.finance.manager.dto.GoalRequest;
import com.finance.manager.dto.GoalResponse;
import com.finance.manager.dto.GoalUpdateRequest;
import com.finance.manager.entity.CategoryType;
import com.finance.manager.entity.Goal;
import com.finance.manager.entity.User;
import com.finance.manager.entity.Transaction;
import com.finance.manager.repository.GoalRepository;
import com.finance.manager.repository.TransactionRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final TransactionRepository transactionRepository;

    public GoalService(GoalRepository goalRepository, TransactionRepository transactionRepository) {
        this.goalRepository = goalRepository;
        this.transactionRepository = transactionRepository;
    }

    public GoalResponse createGoal(GoalRequest request, User user) {
        if (goalRepository.findByGoalNameAndUser(request.getGoalName(), user).isPresent()) {
            throw new IllegalStateException("A goal with this name already exists");
        }

        Goal goal = new Goal();
        goal.setGoalName(request.getGoalName());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setTargetDate(request.getTargetDate());

        if (request.getStartDate() != null) {
            goal.setStartDate(request.getStartDate());
        } else {
            goal.setStartDate(LocalDate.now());
        }
        goal.setUser(user);

        Goal savedGoal = goalRepository.save(goal);

        BigDecimal[] financials = calculateGoalFinancials(user, savedGoal.getStartDate(), savedGoal.getTargetAmount());

        return new GoalResponse(
                savedGoal.getId(),
                savedGoal.getGoalName(),
                savedGoal.getTargetAmount(),
                savedGoal.getStartDate(),
                savedGoal.getTargetDate(),
                financials[0], 
                financials[1].doubleValue(), 
                financials[2]  
        );
    }

    public List<GoalResponse> getAllGoals(User user) {
        List<Goal> goals = goalRepository.findByUser(user);
        List<GoalResponse> responses = new ArrayList<>();

        for (Goal g : goals) {
            BigDecimal[] financials = calculateGoalFinancials(user, g.getStartDate(), g.getTargetAmount());
            
            responses.add(new GoalResponse(
                    g.getId(),
                    g.getGoalName(),
                    g.getTargetAmount(),
                    g.getStartDate(),
                    g.getTargetDate(),
                    financials[0], 
                    financials[1].doubleValue(), 
                    financials[2]  
            ));
        }

        return responses;
    }

    public GoalResponse updateGoal(Long id, GoalUpdateRequest request, User user) {
        Goal goal = goalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        if (request.getTargetAmount() != null) {
            goal.setTargetAmount(request.getTargetAmount());
        }
        if (request.getTargetDate() != null) {
            goal.setTargetDate(request.getTargetDate());
        }

        Goal updatedGoal = goalRepository.save(goal);

        BigDecimal[] financials = calculateGoalFinancials(user, updatedGoal.getStartDate(), updatedGoal.getTargetAmount());

        return new GoalResponse(
                updatedGoal.getId(),
                updatedGoal.getGoalName(),
                updatedGoal.getTargetAmount(),
                updatedGoal.getStartDate(),
                updatedGoal.getTargetDate(),
                financials[0],
                financials[1].doubleValue(),
                financials[2]
        );
    }

    public GoalResponse getGoalById(Long id, User user) {
        Goal goal = goalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        BigDecimal[] financials = calculateGoalFinancials(user, goal.getStartDate(), goal.getTargetAmount());

        return new GoalResponse(
                goal.getId(),
                goal.getGoalName(),
                goal.getTargetAmount(),
                goal.getStartDate(),
                goal.getTargetDate(),
                financials[0], 
                financials[1].doubleValue(), 
                financials[2] 
        );
    }

    public void deleteGoal(Long id, User user) {
        Goal goal = goalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        goalRepository.delete(goal);
    }

    private BigDecimal[] calculateGoalFinancials(User user, LocalDate startDate, BigDecimal targetAmount) {
        List<Transaction> transactions = transactionRepository.findByUserOrderByDateDesc(user);
        
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            if (!t.getDate().isBefore(startDate)) {
                if (t.getCategory().getType() == CategoryType.INCOME) {
                    totalIncome = totalIncome.add(t.getAmount());
                } else if (t.getCategory().getType() == CategoryType.EXPENSE) {
                    totalExpense = totalExpense.add(t.getAmount());
                }
            }
        }

        BigDecimal currentProgress = totalIncome.subtract(totalExpense);
        
        if (currentProgress.compareTo(BigDecimal.ZERO) < 0) {
            currentProgress = BigDecimal.ZERO;
        }

        BigDecimal remainingAmount = targetAmount.subtract(currentProgress);
        if (remainingAmount.compareTo(BigDecimal.ZERO) < 0) {
            remainingAmount = BigDecimal.ZERO; 
        }

        BigDecimal progressPercentage = BigDecimal.ZERO;
        if (targetAmount.compareTo(BigDecimal.ZERO) > 0) {
            progressPercentage = currentProgress.divide(targetAmount, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, java.math.RoundingMode.HALF_UP);
            
            if (progressPercentage.compareTo(new BigDecimal("100")) > 0) {
                progressPercentage = new BigDecimal("100.00");
            }
        }

        return new BigDecimal[]{currentProgress, progressPercentage, remainingAmount};
    }
}