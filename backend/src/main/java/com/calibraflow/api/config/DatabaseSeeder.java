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
                Category.builder().name("Pressão").calibrationIntervalDays(365).description("Manômetros, Transmissores, Pressostatos").build(),
                Category.builder().name("Temperatura").calibrationIntervalDays(365).description("Termômetros, PT-100, Termopares").build(),
                Category.builder().name("Dimensional").calibrationIntervalDays(180).description("Paquímetros, Micrômetros, Trenas").build(),
                Category.builder().name("Elétrica").calibrationIntervalDays(180).description("Multímetros, Alicates Amperímetros, Megômetros").build(),
                Category.builder().name("Massa").calibrationIntervalDays(365).description("Balanças, Pesos Padrão").build(),
                Category.builder().name("Vazão").calibrationIntervalDays(365).description("Medidores de Vazão, Rotâmetros").build(),
                Category.builder().name("Físico-Química").calibrationIntervalDays(180).description("Phmetros, Condutivímetros").build(),
                Category.builder().name("Segurança").calibrationIntervalDays(180).description("Detectores de Gás, Luxímetros").build()
            ));
            System.out.println("Banco de dados populado com todas as categorias padrão da indústria!");
        }
    }
}
