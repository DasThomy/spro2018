package de.sopro.repository;

import de.sopro.model.Organisation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrganisationRepository extends CrudRepository<Organisation, String> {
    List<Organisation> findAll();
}
