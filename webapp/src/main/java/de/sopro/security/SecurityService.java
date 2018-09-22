package de.sopro.security;

import de.sopro.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface SecurityService {

    /**
     * Returns the authenticated User.
     * @return The user or
     */
    Optional<User> getLoggedInUser();

    /**
     *
     * @param userDetails
     */
    void autoLogin(UserDetails userDetails);
}
