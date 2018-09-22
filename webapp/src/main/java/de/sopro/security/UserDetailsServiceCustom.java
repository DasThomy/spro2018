package de.sopro.security;

import de.sopro.model.User;
import de.sopro.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDetailsServiceCustom implements UserDetailsService {

    private Logger logger = LoggerFactory.getLogger(UserDetailsServiceCustom.class);

    @Autowired
    private final UserRepository userRepository;

    public UserDetailsServiceCustom(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(username);

        if(!optionalUser.isPresent()){
            logger.info("User "+ username + " doesn't exist");
            throw new UsernameNotFoundException("Username: "+ username+" not FOUND!");
        }

        return optionalUser.get();
    }
}
