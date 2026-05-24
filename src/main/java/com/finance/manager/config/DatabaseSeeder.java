package com.finance.manager.config;

import com.finance.manager.entity.Category;
import com.finance.manager.entity.CategoryType;
import com.finance.manager.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DatabaseSeeder {

    @Bean
    public CommandLineRunner initDatabase(CategoryRepository categoryRepository) {
        return args -> {
            if (categoryRepository.count() == 0) {

                Category salary = new Category("Salary", CategoryType.INCOME, false, null);
                Category food = new Category("Food", CategoryType.EXPENSE, false, null);
                Category rent = new Category("Rent", CategoryType.EXPENSE, false, null);
                Category transportation = new Category("Transportation", CategoryType.EXPENSE, false, null);
                Category entertainment = new Category("Entertainment", CategoryType.EXPENSE, false, null);
                Category healthcare = new Category("Healthcare", CategoryType.EXPENSE, false, null);
                Category utilities = new Category("Utilities", CategoryType.EXPENSE, false, null);

                categoryRepository.saveAll(List.of(
                        salary, food, rent, transportation, entertainment, healthcare, utilities));
            }
        };
    }
}