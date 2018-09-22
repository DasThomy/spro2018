package de.sopro.security;

import de.sopro.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SecurityServiceImpl implements SecurityService {

    private Logger logger = LoggerFactory.getLogger(SecurityServiceImpl.class);

    @Override
    public Optional<User> getLoggedInUser() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user instanceof User) {
            return Optional.of((User) user);
        }

        return Optional.empty();
    }

    @Override
    public void autoLogin(UserDetails userDetails) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }
}