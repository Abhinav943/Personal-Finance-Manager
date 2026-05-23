package com.finance.manager.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "goals")

public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Goal name cannot be blank")
    @Column(nullable = false)
    private String goalName;

    @NotNull(message = "Target amount is required")
    @Column(nullable = false)
    @Positive(message = "Target amount must be positive")
    private BigDecimal targetAmount;

    @NotNull(message = "Target date is required")
    @Column(nullable = false)
    @Future(message = "Target date must be in the future")
    private LocalDate targetDate;

    @Column(nullable = false)
    private LocalDate startDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Goal() {
    }

    public Goal(String goalName, BigDecimal targetAmount, LocalDate targetDate, User user) {
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
        this.user = user;
    }

    @PrePersist
    protected void onCreate() {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGoalName() { return goalName; }
    public void setGoalName(String goalName) { this.goalName = goalName; }

    public BigDecimal getTargetAmount() { return targetAmount; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
