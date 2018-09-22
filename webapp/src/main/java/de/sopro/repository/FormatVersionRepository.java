package de.sopro.repository;

import de.sopro.model.Format;
import de.sopro.model.FormatVersion;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface FormatVersionRepository extends CrudRepository<FormatVersion, Integer> {
    List<FormatVersion> findAll();

    Optional<FormatVersion> getFormatVersionByFormatAndName(Format format, String name);
}
