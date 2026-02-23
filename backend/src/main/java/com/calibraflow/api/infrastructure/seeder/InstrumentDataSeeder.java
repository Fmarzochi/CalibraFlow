package com.calibraflow.api.infrastructure.seeder;

import com.calibraflow.api.domain.entities.Tenant;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.entities.enums.UserRole;
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
        if (tenantRepository.count() == 0) {
            Tenant tenant = new Tenant();
            tenant.setName("Empresa Padrao");
            tenant.setDocument("00.000.000/0001-00");
            tenantRepository.save(tenant);

            if (userRepository.findOptionalByEmail("admin@calibraflow.com").isEmpty()) {
                User admin = new User();
                admin.setTenant(tenant);
                admin.setName("Administrador");
                admin.setEmail("admin@calibraflow.com");
                admin.setPassword(passwordEncoder.encode("123456"));
                admin.setCpf("00000000000");
                admin.setRole(UserRole.ADMINISTRADOR);
                admin.setEnabled(true);
                userRepository.save(admin);
            }
        }
    }
}