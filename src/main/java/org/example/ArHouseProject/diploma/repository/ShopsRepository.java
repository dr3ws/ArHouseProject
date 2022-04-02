package org.example.ArHouseProject.diploma.repository;

import org.example.ArHouseProject.diploma.models.ShopsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopsRepository extends JpaRepository<ShopsModel, Long> {
    ShopsModel findById(long l);
    ShopsModel findByNameShop(String name);
    ShopsModel findShopsModelByNameShop(String name);
}
