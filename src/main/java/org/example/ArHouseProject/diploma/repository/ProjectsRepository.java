package org.example.ArHouseProject.diploma.repository;

import org.example.ArHouseProject.diploma.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectsRepository extends JpaRepository<ProjectsModel, Long> {
    ProjectsModel findById(long l);
    ProjectsModel findProjectsModelsByRequestsModel(RequestsModel requestsModel);
    List<ProjectsModel> findAllByStatusModel(StatusModel statusModel);
    List<ProjectsModel> findAllByRequestsModel(RequestsModel requestsModel);
}