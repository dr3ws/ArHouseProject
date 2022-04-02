package org.example.ArHouseProject.diploma.repository;

import org.example.ArHouseProject.diploma.models.FurnituresModel;
import org.example.ArHouseProject.diploma.models.FurnituresProjectModel;
import org.example.ArHouseProject.diploma.models.ProjectsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FurnituresProjectRepository extends JpaRepository<FurnituresProjectModel, Long> {
    List<FurnituresProjectModel> findAllByProjectsModel(ProjectsModel projectsModel);
    FurnituresProjectModel findByFurnituresModelAndProjectsModel(FurnituresModel furnitureModel, ProjectsModel projectsModel);
}