package com.example.demo.repository.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import com.example.demo.model.entity.base.BaseEntity;

import java.util.List;


@NoRepositoryBean
public interface BaseRepo<T extends BaseEntity, ID extends Long> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * Получение списка помеченных для удаленных сущностей
     */
    @Query("SELECT t FROM #{#entityName} t WHERE t.deleted = true")
    List<T> findDeleted();

    /**
     * Помечаем для удаления сущность
     *
     * @param id id сущности
     */
    @Query("UPDATE #{#entityName} t SET t.deleted = true WHERE t.id = :id")
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void softDelete(ID id);

    @Query("UPDATE #{#entityName} t SET t.deleted = false WHERE t.id = :id")
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void recover(ID id);

    @Query("SELECT t FROM #{#entityName} t WHERE t.deleted = false")
    List<T> findAllNonDeleted();

    @Query("SELECT t FROM #{#entityName} t WHERE t.deleted = false")
    Page<T> findAllNonDeleted(Pageable pageable);
}
