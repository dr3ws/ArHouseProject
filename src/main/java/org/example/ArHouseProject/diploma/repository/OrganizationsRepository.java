package org.example.ArHouseProject.diploma.repository;

import org.example.ArHouseProject.diploma.models.OrganizationsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationsRepository extends JpaRepository<OrganizationsModel, Long> {
    OrganizationsModel findById(long l);
    OrganizationsModel findOrganizationsModelByNameOrganizationAndInnAndOgrn(String name, String inn, String ogrn);
}