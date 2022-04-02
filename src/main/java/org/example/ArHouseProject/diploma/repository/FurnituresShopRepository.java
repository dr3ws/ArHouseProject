package org.example.ArHouseProject.diploma.repository;

import org.example.ArHouseProject.diploma.models.FurnituresModel;
import org.example.ArHouseProject.diploma.models.FurnituresShopModel;
import org.example.ArHouseProject.diploma.models.ShopsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FurnituresShopRepository extends JpaRepository<FurnituresShopModel, Long> {
    FurnituresShopModel findById(long l);
    FurnituresShopModel findFurnituresShopModelById(Long l);
    List<FurnituresShopModel> findAllByShopsModel(ShopsModel shopsModel);
    List<FurnituresShopModel> findAllByFurnituresModel(FurnituresModel furnitureModel);
    FurnituresShopModel findFurnituresShopModelByFurnituresModelAndShopsModel(FurnituresModel furnituresModel, ShopsModel shopsModel);
}