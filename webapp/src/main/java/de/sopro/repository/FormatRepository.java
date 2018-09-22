package de.sopro.repository;

import de.sopro.model.Format;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FormatRepository extends CrudRepository<Format, Integer> {

    Optional<Format> findFormatByName(String name);
    Optional<Format> findFormatById(int Id);

    List<Format> findAll();
}
