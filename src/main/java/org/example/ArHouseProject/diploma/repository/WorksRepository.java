package org.example.ArHouseProject.diploma.repository;

import org.example.ArHouseProject.diploma.models.WorksModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorksRepository extends JpaRepository<WorksModel, Long> {
    WorksModel findById(long l);
    WorksModel findByUsername(String username);
}