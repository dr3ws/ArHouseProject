package org.example.ArHouseProject.diploma.models;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Data
public class ShopsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotEmpty(message = "Введите название")
    private String nameShop;
    @NotEmpty(message = "Введите адрес")
    private String address;
    private String telephone;

    @ManyToOne
    private OrganizationsModel organizationsModel;
}