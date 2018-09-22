package de.sopro.repository;

import de.sopro.model.Tag;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TagRepository extends CrudRepository<Tag, String> {
    List<Tag> findAll();

    List<Tag> findByNameContainingIgnoreCase(String name);

}
