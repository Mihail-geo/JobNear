package pro.cproject.lkpassengerbackend.admin.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.cproject.lkpassengerbackend.admin.model.entity.ModuleAccessEntity;
import pro.cproject.lkpassengerbackend.admin.model.enumeration.Module;
import pro.cproject.lkpassengerbackend.admin.repository.ModuleAccessRepo;
import pro.cproject.lkpassengerbackend.admin.service.ModuleService;

import java.util.List;
import java.util.Set;

/**
 * @author vladi_geras on 22.05.2019
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class ModuleServiceImpl implements ModuleService {
	private final ModuleAccessRepo moduleAccessRepo;

	@Override
	public Set<Module> get() {
		return Set.of(Module.values());
	}

	@Transactional
	@Override
	public List<ModuleAccessEntity> findAccess() {
		return moduleAccessRepo.findAll();
	}
}
