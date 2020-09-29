package com.example.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pro.cproject.lkpassengerbackend.admin.model.dto.manager.save.CreateUpdateManager;
import pro.cproject.lkpassengerbackend.admin.model.dto.restore.changePassword.ChangePasswordRequest;
import pro.cproject.lkpassengerbackend.admin.model.entity.ManagerEntity;

import java.util.Optional;


public interface ManagerService {
	Optional<ManagerEntity> find(Long managerId);

	Optional<ManagerEntity> findByEmail(String email);

	Page<ManagerEntity> find(Pageable pageable, String name);

	void block(Long userId, Boolean lock);

	ManagerEntity save(CreateUpdateManager manager, Long managerId);

	void resetPassword(String email);

	void changePassword(ChangePasswordRequest changePasswordRequest);
}
