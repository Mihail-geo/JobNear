package com.example.demo.model.entity.base;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;


@NoArgsConstructor
@Setter
@Getter
@MappedSuperclass
public class LongIdEntity implements Serializable {
    private static final long serialVersionUID = -4908825607587224264L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
}
