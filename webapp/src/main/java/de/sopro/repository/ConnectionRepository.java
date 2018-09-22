package de.sopro.repository;

import de.sopro.model.Connection;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConnectionRepository extends CrudRepository<Connection, Integer> {
    List<Connection> findAll();
}
