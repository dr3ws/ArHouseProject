package org.example.ArHouseProject.diploma.repository;

import org.example.ArHouseProject.diploma.models.RequestsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestsRepository extends JpaRepository<RequestsModel, Long> {
    RequestsModel findById(long id);
    RequestsModel findByToken(String token);
    RequestsModel findByNameProject(String nameProject);
}