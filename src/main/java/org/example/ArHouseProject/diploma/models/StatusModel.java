package org.example.ArHouseProject.diploma.models;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
public class StatusModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String status; // статус
    private String destination; // назначение
}