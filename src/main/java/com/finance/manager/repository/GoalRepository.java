package com.finance.manager.repository;

import com.finance.manager.entity.Goal;
import com.finance.manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUser(User user);
}