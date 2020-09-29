package com.example.demo;

import com.example.demo.model.embedded.NameEmbedded;
import com.example.demo.model.entity.ManagerEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.repository.ManagerRepo;
import com.example.demo.service.ModuleService;
import com.example.demo.util.Validator;

@RequiredArgsConstructor
@Slf4j
@Component
public class Initializer implements CommandLineRunner {
    private ManagerRepo managerRepo;
    private PasswordEncoder passwordEncoder;
    private ModuleService moduleService;

    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.password}")
    private String adminPassword;

    @Transactional
    @Override
    public void run(String... args) {
        checkModules();
        createAdmin();
    }

    // этот метод используем для того, чтобы проверить, нет ли у пользователя модулей, которые
    // уже не состоят в enum. Если коллекция пользователей получена таким образом успешно, то
    // 'лишних' модулей в базе нету.
    private void checkModules() {
        try {
            moduleService.findAccess();
        } catch (InvalidDataAccessApiUsageException e) {
            log.error("В базе данных у пользователя найден модуль, который не учтен в Enum. Эти записи необходимо очистить", e);
            System.exit(1);
        }
    }

    private void createAdmin() {
        if (Validator.isEmailValid(adminEmail) && managerRepo.findByEmail(adminEmail).isEmpty()) {
            ManagerEntity admin = new ManagerEntity();
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setEmail(adminEmail);
            admin.setName(new NameEmbedded("Администратор", "Администратор", "Администратор"));
            admin.setAdmin(true);
            managerRepo.save(admin);
            log.info("Создан администратор системы");
        }
    }
}
