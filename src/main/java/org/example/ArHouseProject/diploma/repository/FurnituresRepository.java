package org.example.ArHouseProject.diploma.repository;

import org.example.ArHouseProject.diploma.models.FurnituresModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FurnituresRepository extends JpaRepository<FurnituresModel, Long> {
    FurnituresModel findById(long l);
    FurnituresModel findFurnitureModelById(long l);
    FurnituresModel findFurnitureModelByArticul(String articul);
}