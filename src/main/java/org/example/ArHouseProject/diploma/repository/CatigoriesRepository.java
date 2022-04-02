package org.example.ArHouseProject.diploma.repository;

import org.example.ArHouseProject.diploma.models.CategoriesModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatigoriesRepository extends JpaRepository<CategoriesModel, Long> {
    CategoriesModel findCategoriesModelByCategoriesAndParentCategories(String categories, String parentcategories);
}