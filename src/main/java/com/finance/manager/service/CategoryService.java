package com.finance.manager.service;

import org.springframework.stereotype.Service;

import com.finance.manager.dto.CategoryRequest;
import com.finance.manager.dto.CategoryResponse;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.User;
import com.finance.manager.repository.CategoryRepository;
import com.finance.manager.repository.TransactionRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public CategoryService(CategoryRepository categoryRepository, TransactionRepository transactionRepository) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<CategoryResponse> getAllCategories(User user) {
        List<Category> defaults = categoryRepository.findByUserIsNull();

        List<Category> customs = categoryRepository.findByUser(user);

        List<CategoryResponse> responses = new ArrayList<>();
        for (Category c : defaults) {
            responses.add(new CategoryResponse(c.getName(), c.getType(), c.isCustom()));
        }
        for (Category c : customs) {
            responses.add(new CategoryResponse(c.getName(), c.getType(), c.isCustom()));
        }

        return responses;
    }

    public CategoryResponse createCustomCategory(CategoryRequest request, User user) {
        if (categoryRepository.findByNameAndUserIsNull(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Cannot create a custom category with a default system name");
        }

        if (categoryRepository.findByNameAndUser(request.getName(), user).isPresent()) {
            throw new IllegalStateException("Category name already exists");
        }

        Category newCategory = new Category(request.getName(), request.getType(), true, user);
        categoryRepository.save(newCategory);

        return new CategoryResponse(newCategory.getName(), newCategory.getType(), newCategory.isCustom());
    }

    public void deleteCustomCategory(String name, User user) {
        if (categoryRepository.findByNameAndUserIsNull(name).isPresent()) {
            throw new IllegalArgumentException("Cannot delete default system categories");
        }

        Category category = categoryRepository.findByNameAndUser(name, user)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (transactionRepository.existsByCategory(category)) {
            throw new IllegalStateException("Cannot delete category currently referenced by transactions");
        }

        categoryRepository.delete(category);
    }
}
