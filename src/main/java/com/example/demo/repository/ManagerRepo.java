package com.example.demo.repository;

import com.example.demo.repository.base.BaseRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.demo.model.entity.ManagerEntity;
import java.util.Optional;

@Repository
public interface ManagerRepo extends BaseRepo<ManagerEntity, Long> {
    Optional<ManagerEntity> findByEmail(String email);

    @Query(value = "SELECT m FROM ManagerEntity m " +
            "WHERE UPPER(CONCAT(COALESCE(m.name.lastName, '') , COALESCE(m.name.firstName, ''), COALESCE(m.name.middleName, ''))) LIKE UPPER(CONCAT('%', :name, '%'))")
    Page<ManagerEntity> searchByName(String name, Pageable pageable);
}