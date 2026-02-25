package com.calibraflow.api.infrastructure.seeder;

import com.calibraflow.api.domain.entities.Tenant;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.repositories.TenantRepository;
import com.calibraflow.api.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstrumentDataSeeder implements CommandLineRunner {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Tenant tenant1 = tenantRepository.findById(1L).orElseGet(() -> {
            Tenant newTenant = new Tenant();
            newTenant.setName("Empresa Piloto");
            newTenant.setDocument("00.000.000/0001-00");
            return tenantRepository.save(newTenant);
        });

        if (userRepository.findOptionalByEmail("admin@calibraflow.com").isEmpty()) {
            User admin = new User();
            admin.setTenant(tenant1);
            admin.setName("Administrador");
            admin.setEmail("admin@calibraflow.com");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setCpf("00000000000");
            admin.setRole("ADMINISTRADOR");
            admin.setEnabled(true);
            userRepository.save(admin);
        }

        Tenant tenant2 = tenantRepository.findById(2L).orElseGet(() -> {
            Tenant newTenant = new Tenant();
            newTenant.setName("Empresa Piloto 2 (Engenharia)");
            newTenant.setDocument("00.000.000/0002-00");
            return tenantRepository.save(newTenant);
        });

        if (userRepository.findOptionalByEmail("admin2@calibraflow.com").isEmpty()) {
            User admin2 = new User();
            admin2.setTenant(tenant2);
            admin2.setName("Administrador 2");
            admin2.setEmail("admin2@calibraflow.com");
            admin2.setPassword(passwordEncoder.encode("123456"));
            admin2.setCpf("11111111111");
            admin2.setRole("ADMINISTRADOR");
            admin2.setEnabled(true);
            userRepository.save(admin2);
        }
    }
}