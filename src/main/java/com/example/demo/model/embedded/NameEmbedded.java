package com.example.demo.model.embedded;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;


@ApiModel(description = "Имя")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Embeddable
public class NameEmbedded implements Serializable {
    private static final long serialVersionUID = -2057000514902415977L;

    @ApiModelProperty(value = "Имя")
    @Column(name = "first_name")
    private String firstName;

    @ApiModelProperty(value = "Фамилия")
    @Column(name = "last_name")
    private String lastName;

    @ApiModelProperty(value = "Отчество")
    @Column(name = "middle_name")
    private String middleName;
}