package com.finance.manager.controller;

import com.finance.manager.dto.CategoryRequest;
import com.finance.manager.dto.CategoryResponse;
import com.finance.manager.dto.MessageResponse;
import com.finance.manager.entity.User;
import com.finance.manager.repository.UserRepository;
import com.finance.manager.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    
    private final CategoryService categoryService;
    private final UserRepository userRepository;

    public CategoryController(CategoryService categoryService, UserRepository userRepository) {
        this.categoryService = categoryService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        User user = getAuthenticatedUser();
        List<CategoryResponse> categories = categoryService.getAllCategories(user);
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCustomCategory(@Valid @RequestBody CategoryRequest request) {
        User user = getAuthenticatedUser();
        CategoryResponse response = categoryService.createCustomCategory(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<MessageResponse> deleteCategory(@PathVariable String name) {
        User currentUser = getAuthenticatedUser();
        categoryService.deleteCustomCategory(name, currentUser);
        
        return ResponseEntity.ok(new MessageResponse("Category deleted successfully"));
    }
}
