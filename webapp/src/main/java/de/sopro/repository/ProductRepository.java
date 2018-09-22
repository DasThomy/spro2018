package de.sopro.repository;

import de.sopro.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Integer> {
    List<Product> findAll();

    List<Optional<Product>> getProductsByName(String name);

    Optional<Product> getProductById(int id);

    Boolean existsByNameAndVersion(String name, String version);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> getProductByLogoName(String logoName);

    List<Product> findByNameContainingIgnoreCaseAndCertified(String name, boolean certified);

    List<Product> findByCertified(boolean certified);
}
