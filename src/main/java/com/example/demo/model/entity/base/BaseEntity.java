package com.example.demo.model.entity.base;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@NoArgsConstructor
@Setter
@Getter
@MappedSuperclass
public class BaseEntity extends LongIdEntity {
    private static final long serialVersionUID = 2810291579707753616L;

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now(ZoneOffset.UTC);

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}
