package com.example.demo.service;

import com.example.demo.model.entity.ModuleAccessEntity;

import java.util.List;
import java.util.Set;


public interface ModuleService {
	Set<Module> get();

	List<ModuleAccessEntity> findAccess();
}
