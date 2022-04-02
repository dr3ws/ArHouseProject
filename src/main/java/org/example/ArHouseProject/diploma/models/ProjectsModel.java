package org.example.ArHouseProject.diploma.models;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
public class ProjectsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String pathToModel;
    private Boolean approvedReport; // одобренный отчёт
    @OneToOne
    private StatusModel statusModel;
    @OneToOne
    private RequestsModel requestsModel;
}
