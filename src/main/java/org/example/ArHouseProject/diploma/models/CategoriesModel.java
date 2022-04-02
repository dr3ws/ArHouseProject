package org.example.ArHouseProject.diploma.models;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
public class CategoriesModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String categories; // категории
    private String parentCategories; // категории
}