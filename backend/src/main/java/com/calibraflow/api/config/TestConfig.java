package com.calibraflow.api.config;

import com.calibraflow.api.domain.entities.Category;
import com.calibraflow.api.domain.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;

@Configuration
@Profile("default")
public class TestConfig implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {

        // Eu removo o que estiver lá para garantir que teremos a lista limpa e oficial
        categoryRepository.deleteAll();

        Category c1 = new Category(null, "Pressão", 365, "Instrumentos de medição de pressão e vácuo");
        Category c2 = new Category(null, "Temperatura", 365, "Termômetros, termopares e controladores");
        Category c3 = new Category(null, "Dimensional", 730, "Paquímetros, micrômetros e réguas");
        Category c4 = new Category(null, "Massa", 365, "Balanças analíticas e industriais");
        Category c5 = new Category(null, "Volume", 365, "Vidrarias e medidores de vazão");

        categoryRepository.saveAll(Arrays.asList(c1, c2, c3, c4, c5));

        System.out.println(">>> CalibraFlow: BANCO RESETADO E CATEGORIAS IMPORTADAS!");
    }
}