package de.sopro.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sopro.DTO.UserDTO;
import de.sopro.model.Organisation;
import de.sopro.model.User;
import de.sopro.repository.OrganisationRepository;
import de.sopro.repository.UserRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final OrganisationRepository organisationRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, OrganisationRepository organisationRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.organisationRepository = organisationRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public List<User> getUsersByEMailSearch(String search) {
        return userRepository.findByEmailContaining(search);
    }

    @Transactional
    public User makeAdministrator(String jsonUser) throws IOException, NotFoundException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonUser);


        String email = jsonNode.findValue("email").asText();
        String role = jsonNode.findValue("role").asText();

        Optional<User> optUser = userRepository.findByEmail(email);
        if (!optUser.isPresent()) {
            throw new NotFoundException("User not found");
        }

        User user = optUser.get();
        user.setRole(role);
        userRepository.save(user);
        return user;
    }


    @Transactional
    public User registerNewUserAccount(UserDTO userDTO) throws UserAlreadyExistsException {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        User user = new User(userDTO.getEmail(), userDTO.getFirstName(), userDTO.getLastName());

        Optional<Organisation> optionalOrganisation = organisationRepository.findById(userDTO.getOrganisation());
        Organisation organisation = optionalOrganisation.orElse(new Organisation(userDTO.getOrganisation()));

        user.setTitle(userDTO.getTitle());
        user.setOrganisation(organisation);
        user.setFormOfAdress(userDTO.getFormOfAddress());
        user.setRole("ROLE_USER");
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));

        userRepository.save(user);
        return user;
    }

    @Transactional
    public User changeUserAccount(UserDTO userDTO) throws UserAlreadyExistsException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        checkIfDifferentAndSet(user, userDTO);


        return user;
    }

    /**
     * Checks if the data changed and change the Data in the persistence if
     *
     * @param user
     * @param userDTO
     */
    private void checkIfDifferentAndSet(User user, UserDTO userDTO) {

        //check if the data is different to the old one and if it is filled
        if (!user.getTitle().equals(userDTO.getTitle()) && !userDTO.getTitle().isEmpty()) {
            user.setTitle(userDTO.getTitle());
        }

        if (!user.getFormOfAdress().equals(userDTO.getFormOfAddress()) && !userDTO.getFormOfAddress().isEmpty()) {
            user.setFormOfAdress(userDTO.getFormOfAddress());
        }

        if (!user.getForename().equals(userDTO.getFirstName()) && !userDTO.getFirstName().isEmpty()) {
            user.setForename(userDTO.getFirstName());
        }

        if (!userDTO.getLastName().equals(user.getSurname()) && !userDTO.getLastName().isEmpty()) {
            user.setSurname(userDTO.getLastName());
        }


        //Organisation Stuff
        Optional<Organisation> organisation = organisationRepository.findById((userDTO.getOrganisation()));
        if (organisation.isPresent()) {
            if (!user.getOrganisation().equals(organisation.get())) {
                user.setOrganisation(organisation.get());
            }
        } else {
            Organisation newOrganisation = new Organisation(userDTO.getOrganisation());
            organisationRepository.save(newOrganisation);
            user.setOrganisation(newOrganisation);
        }

        if (!bCryptPasswordEncoder.encode(user.getPassword()).equals(bCryptPasswordEncoder.encode(userDTO.getPassword()))) {
            user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        }

        userRepository.save(user);

    }


    public class UserAlreadyExistsException extends Exception {
    }
}
