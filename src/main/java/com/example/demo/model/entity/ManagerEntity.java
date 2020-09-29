package com.example.demo.model.entity;

import com.example.demo.model.embedded.NameEmbedded;
import com.example.demo.model.entity.base.BaseEntity;
import com.example.demo.model.enumeration.ManagerStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "managers")
public class ManagerEntity extends BaseEntity {
    private static final long serialVersionUID = 6058579063341961748L;

    @Embedded
    private NameEmbedded name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "admin", nullable = false)
    private boolean admin = false;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ManagerStatus status = ManagerStatus.ACTIVATED;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "manager", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ModuleAccessEntity> access = new HashSet<>();

    public void addAccess(ModuleAccessEntity moduleAccess) {
        if (access == null) access = new HashSet<>();

        if (moduleAccess != null) {
            moduleAccess.setManager(this);
            access.add(moduleAccess);
        }
    }

    public void removeAccess() {
        if (access == null) {
            access = new HashSet<>();
        } else {
            for (Iterator<ModuleAccessEntity> accessIterator = access.iterator(); accessIterator.hasNext(); ) {
                accessIterator.next().setManager(null);
                accessIterator.remove();
            }
        }
    }
}
