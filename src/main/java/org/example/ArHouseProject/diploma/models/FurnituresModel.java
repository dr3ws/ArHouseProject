package org.example.ArHouseProject.diploma.models;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Data
public class FurnituresModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String articul;
    private String nameFurniture;
    private String pathToFurniture;
    @OneToOne
    private CategoriesModel categoriesModel;
}