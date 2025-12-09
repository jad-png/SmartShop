package com.jyoxin.smartshop.core.seeder;

import com.jyoxin.smartshop.entity.User;
import com.jyoxin.smartshop.entity.enums.Role;
import com.jyoxin.smartshop.repository.UserRepository;
import com.jyoxin.smartshop.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class UserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            log.info("🔐 Seeding admin user...");

            User admin = User.builder()
                    .username("admin")
                    .password(PasswordUtil.hash("admin123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);

            log.info("Admin user seeded (username: admin, password: admin123)");
        } else {
            log.info("ℹAdmin user already exists, skipping...");
        }
    }
}
