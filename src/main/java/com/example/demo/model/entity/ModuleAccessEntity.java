package com.example.demo.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.demo.model.entity.base.LongIdEntity;
import com.example.demo.model.enumeration.Module;
import com.example.demo.model.enumeration.ModuleScope;

import javax.persistence.*;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "managers_modules")
public class ModuleAccessEntity extends LongIdEntity {
    private static final long serialVersionUID = 2684659049683446321L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private ManagerEntity manager;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "module", nullable = false)
    private Module module;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "scope", nullable = false)
    private ModuleScope scope = ModuleScope.READ;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModuleAccessEntity that = (ModuleAccessEntity) o;

        if (manager != null ? !manager.equals(that.manager) : that.manager != null) return false;
        if (module != that.module) return false;
        return scope == that.scope;
    }

    @Override
    public int hashCode() {
        int result = manager != null ? manager.hashCode() : 0;
        result = 31 * result + (module != null ? module.hashCode() : 0);
        result = 31 * result + (scope != null ? scope.hashCode() : 0);
        return result;
    }
}