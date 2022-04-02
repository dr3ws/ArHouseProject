package org.example.ArHouseProject.diploma.models;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
public class FurnituresProjectModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long count;
    @OneToOne
    private FurnituresModel furnituresModel;
    @OneToOne
    private ProjectsModel projectsModel;
}