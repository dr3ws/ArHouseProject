package org.example.ArHouseProject.diploma.models;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Data
public class FurnituresShopModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull(message = "Введите количество")
    private Long count;
    @NotNull(message = "Введите цену")
    private Long price;
    private Long totalPrice;
    @OneToOne
    private FurnituresModel furnituresModel;
    @OneToOne
    private ShopsModel shopsModel;
}