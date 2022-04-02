package org.example.ArHouseProject.diploma.repository;

import org.example.ArHouseProject.diploma.models.StatusModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<StatusModel, Long> {
    StatusModel findById(long id);
}
