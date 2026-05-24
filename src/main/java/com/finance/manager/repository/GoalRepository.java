package com.finance.manager.repository;

import com.finance.manager.entity.Goal;
import com.finance.manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUser(User user);

    Optional<Goal> findByIdAndUser(Long id, User user);

    Optional<Goal> findByGoalNameAndUser(String goalName, User user);
}