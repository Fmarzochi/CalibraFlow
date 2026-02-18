package com.calibraflow.api.config;

import com.calibraflow.api.domain.entities.Category;
import com.calibraflow.api.domain.repositories.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DatabaseSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public DatabaseSeeder(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            categoryRepository.saveAll(List.of(
                    Category.builder().name("Pressão").calibrationIntervalDays(365).description("Manômetros e Transmissores").build(),
                    Category.builder().name("Temperatura").calibrationIntervalDays(365).description("Termômetros e PT-100").build(),
                    Category.builder().name("Dimensional").calibrationIntervalDays(180).description("Paquímetros e Micrômetros").build(),
                    Category.builder().name("Elétrica").calibrationIntervalDays(180).description("Multímetros e Alicates").build(),
                    Category.builder().name("Massa").calibrationIntervalDays(365).description("Balanças e Pesos Padrão").build()
            ));
            System.out.println("Banco de dados populado com categorias padrão!");
        }
    }
}