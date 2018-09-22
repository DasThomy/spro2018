package de.sopro.repository;

import de.sopro.model.Combination;
import de.sopro.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends CrudRepository<User, Integer> {

    List<User> findAll();

    @Query("select u from User u, Combination c where c.creator = u")
    List<Combination> getOwnCombinations(User user);

    Optional<User> findByEmail(String email);

    List<User> findByEmailContaining(String email);

}
