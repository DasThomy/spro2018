package de.sopro.repository;

import de.sopro.model.ProductInComb;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductInCombRepository extends CrudRepository<ProductInComb, Integer> {
    List<ProductInComb> findAll();

}
