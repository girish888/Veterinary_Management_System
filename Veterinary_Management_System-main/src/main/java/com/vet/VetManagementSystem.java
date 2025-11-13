package com.vet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.vet.model.User;
import com.vet.model.Role;
import com.vet.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EntityScan("com.vet.model")
@EnableJpaRepositories("com.vet.repository")
@EnableScheduling
public class VetManagementSystem {
    public static void main(String[] args) {
        SpringApplication.run(VetManagementSystem.class, args);
    }

    @Bean
    public static CommandLineRunner createAdmin(ApplicationContext ctx) {
        return args -> {
            UserRepository userRepository = ctx.getBean(UserRepository.class);
            PasswordEncoder passwordEncoder = ctx.getBean(PasswordEncoder.class);
            
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setFullName("Administrator");
                admin.setEmail("admin@vet.com");
                admin.setRole(Role.ADMIN);
                admin.setBlocked(false);
                userRepository.save(admin);
                System.out.println("Default admin user created: admin / admin123");
            }
        };
    }
} 