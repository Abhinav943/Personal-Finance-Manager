package com.finance.manager.controller;

import com.finance.manager.dto.GoalListResponse;
import com.finance.manager.dto.GoalRequest;
import com.finance.manager.dto.GoalResponse;
import com.finance.manager.dto.GoalUpdateRequest;
import com.finance.manager.entity.User;
import com.finance.manager.repository.UserRepository;
import com.finance.manager.service.GoalService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;
    private final UserRepository userRepository;

    public GoalController(GoalService goalService, UserRepository userRepository) {
        this.goalService = goalService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public ResponseEntity<GoalListResponse> getAllGoals() {
        User user = getAuthenticatedUser();
        List<GoalResponse> goals = goalService.getAllGoals(user);
        return ResponseEntity.ok(new GoalListResponse(goals));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getGoal(@PathVariable Long id) {
        User user = getAuthenticatedUser();
        GoalResponse response = goalService.getGoalById(id, user);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(@Valid @RequestBody GoalRequest request) {
        User user = getAuthenticatedUser();
        GoalResponse response = goalService.createGoal(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(@PathVariable Long id, @Valid @RequestBody GoalUpdateRequest request) {
        User user = getAuthenticatedUser();
        GoalResponse response = goalService.updateGoal(id, request, user);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        User user = getAuthenticatedUser();
        goalService.deleteGoal(id, user);
        return ResponseEntity.noContent().build();
    }
}