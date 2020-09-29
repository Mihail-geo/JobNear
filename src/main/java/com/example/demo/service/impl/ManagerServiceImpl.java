package pro.cproject.lkpassengerbackend.admin.service.impl;

import com.example.demo.repository.ManagerRepo;
import com.example.demo.service.ManagerService;
import com.example.demo.util.HttpUtil;
import com.example.demo.util.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.cproject.lkpassengerbackend.admin.exception.NotFoundException;
import pro.cproject.lkpassengerbackend.admin.exception.ServerException;
import pro.cproject.lkpassengerbackend.admin.model.dto.manager.save.CreateUpdateManager;
import pro.cproject.lkpassengerbackend.admin.model.dto.manager.save.ModuleAccess;
import pro.cproject.lkpassengerbackend.admin.model.dto.restore.changePassword.ChangePasswordRequest;
import pro.cproject.lkpassengerbackend.admin.model.entity.ManagerEntity;
import pro.cproject.lkpassengerbackend.admin.model.entity.ModuleAccessEntity;
import pro.cproject.lkpassengerbackend.admin.model.entity.embedded.NameEmbedded;
import pro.cproject.lkpassengerbackend.admin.model.enumeration.ManagerStatus;
import pro.cproject.lkpassengerbackend.admin.util.PasswordGenerator;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class ManagerServiceImpl implements ManagerService {
	private final ManagerRepo managerRepo;
	private final PasswordEncoder passwordEncoder;

	@Transactional(readOnly = true)
	@Override
	public Optional<ManagerEntity> find(Long managerId) {
		if (!Validator.isLongValid(managerId)) throw new ServerException("Некорректное значение параметра 'managerId'");
		return managerRepo.findById(managerId);
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<ManagerEntity> findByEmail(String email) {
		if (!Validator.isEmailValid(email))
			throw new ServerException("Некорректное значение параметра 'email'");
		return managerRepo.findByEmail(email);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<ManagerEntity> find(Pageable pageable, String name) {
		return name == null ? managerRepo.findAllNonDeleted(pageable) : managerRepo.searchByName(name, pageable);
	}

	@Transactional
	@Override
	public void block(Long managerId, Boolean lock) {
		if (!Validator.isLongValid(managerId)) throw new ServerException("Некорректное значение параметра 'managerId'");
		ManagerEntity managerEn = find(managerId)
				.orElseThrow(() -> new NotFoundException("Менеджер с id = " + managerId));

		managerEn.setStatus((lock == null || !lock) ? ManagerStatus.ACTIVATED : ManagerStatus.BLOCKED);
		managerRepo.save(managerEn);
		log.info("Менеджер с id = " + managerId + " был " + ((lock == null || !lock) ? "разблокирован" : "заблокирован"));
	}

	@Transactional
	@Override
	public ManagerEntity save(CreateUpdateManager manager, Long managerId) {
		if (manager == null) throw new ServerException("Некорректное значение параметра 'manager'");

		ManagerEntity managerEntity = getManagerEntityOrCreateNew(manager, managerId);
		setDataFromDtoToEntity(manager, managerEntity);
		managerRepo.save(managerEntity);

		if (managerId == null) {
			log.info("Менеджер с email = " + manager.getEmail() + " был зарегистрирован");
		} else {
			log.info("Менеджер с email = " + manager.getEmail() + " был обновлен");
		}
		return managerEntity;
	}

	@Transactional
	@Override
	public void resetPassword(String email) {
		if (!Validator.isEmailValid(email)) throw new ServerException("Некорректное значение параметра 'email'");

		Optional<ManagerEntity> optionalUserEntity = managerRepo.findByEmail(email);
		if (optionalUserEntity.isPresent()) {
			ManagerEntity managerEn = optionalUserEntity.get();

			String password = PasswordGenerator
					.builder()
					.useDigits(true)
					.usePunctuation(true)
					.useUpper(true)
					.useLower(true)
					.build()
					.generate(10);

			managerEn.setPassword(passwordEncoder.encode(password));


			managerRepo.save(managerEn);
			log.info("Менеджер с email = " + email + " восстановил пароль");
		} else {
			throw new ServerException("Не найден пользователь с таким email", 404);
		}
	}

	@Transactional
	@Override
	public void changePassword(ChangePasswordRequest changePasswordRequest) {
		if (changePasswordRequest == null)
			throw new ServerException("Некорректное значение параметра 'changePasswordRequest'");

		String currentPassword = changePasswordRequest.getCurrentPassword();
		if (Validator.isEmpty(currentPassword))
			throw new ServerException("Указан пустой текущий пароль");

		String newPassword = changePasswordRequest.getNewPassword();
		if (Validator.isEmpty(newPassword))
			throw new ServerException("Указан пустой новый пароль");

		Long managerId = HttpUtil.getManagerIdFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
		if (managerId == null) throw new ServerException("Пустой manager id из объекта аутентификации");

		ManagerEntity managerEntity = find(managerId)
				.orElseThrow(() -> new NotFoundException("Менеджер с id = " + managerId));

		if (!passwordEncoder.matches(currentPassword, managerEntity.getPassword())) {
			throw new ServerException("Неверно указан текущий пароль");
		}

		managerEntity.setPassword(passwordEncoder.encode(newPassword));
		managerRepo.save(managerEntity);
	}

	private Set<ModuleAccessEntity> convertModules(Set<ModuleAccess> modules) {
		if (modules == null) return new HashSet<>();

		return modules
				.stream()
				.map(moduleAccess -> {
							ModuleAccessEntity moduleAccessEntity = new ModuleAccessEntity();
							moduleAccessEntity.setModule(moduleAccess.getModule());
							moduleAccessEntity.setScope(moduleAccess.getScope());
							return moduleAccessEntity;
						}
				)
				.collect(Collectors.toSet());
	}

	private void setDataFromDtoToEntity(CreateUpdateManager from, ManagerEntity to) {
		NameEmbedded name = from.getName();
		if (name == null) throw new ServerException("Некорректное значение параметра 'name'");
		if (Validator.isEmpty(name.getFirstName())) throw new ServerException("Не указано имя менеджера");
		if (Validator.isEmpty(name.getLastName())) throw new ServerException("Не указана фамилия менеджера");

		to.setEmail(from.getEmail());
		to.setName(from.getName());
		to.setAdmin(from.isAdmin());

		if (!from.isAdmin()) {
			if (from.getAccess() == null || from.getAccess().isEmpty()) {
				throw new ServerException("Не указан(ы) доступ(ы)");
			}

			if (to.getId() != null) {
				// во время обновления удаляем сначала старые доступы
				to.removeAccess();
			}

			for (ModuleAccessEntity access : convertModules(from.getAccess())) {
				to.addAccess(access);
			}
		}
	}

	private ManagerEntity getManagerEntityOrCreateNew(CreateUpdateManager manager, Long managerId) {
		ManagerEntity managerEntity;
		String password = PasswordGenerator
				.builder()
				.useDigits(true)
				.useUpper(true)
				.useLower(true)
				.build()
				.generate(10);

		String email = manager.getEmail();
		if (managerId == null) {
			if (!Validator.isEmailValid(email))
				throw new ServerException("Некорректное значение параметра 'email'");
			if (managerRepo.findByEmail(email).isPresent()) {
				throw new ServerException("Адрес электронной почты '" + email + "' уже используется");
			}
			managerEntity = new ManagerEntity();
			managerEntity.setPassword(passwordEncoder.encode(password));

		} else {
			managerEntity = find(managerId)
					.orElseThrow(() -> new NotFoundException("Менеджер с id = " + managerId));

			// если email указан новый, то его проверяем на уникальность
			if (Validator.isEmailValid(email) && !email.equals(managerEntity.getEmail())) {
				if (managerRepo.findByEmail(email).isPresent()) {
					throw new ServerException("Адрес электронной почты '" + email + "' уже используется");
				}
			}
		}
		return managerEntity;
	}
}
