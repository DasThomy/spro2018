package de.sopro.repository;

import de.sopro.model.Combination;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CombinationRepository extends CrudRepository<Combination, Integer> {
    List<Combination> findAll();

    @Query(value = "select c from Combination c where c.publicVisible = true")
    List<Combination> findAllPublic();

    List<Combination> findByNameContainingIgnoreCase(String searchRequest);
}
