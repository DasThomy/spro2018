package de.sopro.controller;

import de.sopro.model.User;
import de.sopro.repository.UserRepository;
import de.sopro.DTO.UserDTO;
import de.sopro.security.SecurityService;
import de.sopro.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.Optional;

@Controller
public class ProfileController {

    private final SecurityService securityService;
    private final UserService userService;
    private final UserRepository userrepo;

    @Autowired
    public ProfileController(UserService userService, UserRepository userrepo, SecurityService securityService) {
        this.userService = userService;
        this.userrepo = userrepo;
        this.securityService = securityService;
    }

    @GetMapping("/profile")
    public ModelAndView showProfile() {
        ModelAndView mv = new ModelAndView("profile");
        UserDTO userDTO = new UserDTO();
        mv.addObject("user", userDTO);

        //Adding the user data to the screen

        Optional<User> user = securityService.getLoggedInUser();
        if (!user.isPresent()) {
            mv.setViewName("login");
            mv.setStatus(HttpStatus.UNAUTHORIZED);
            return mv;
        }

        String username = user.get().getEmail();
        User currentUser = userrepo.findByEmail(username).get();

        mv.addObject("userFormOfAddress", currentUser.getFormOfAdress());
        mv.addObject("userTitle", currentUser.getTitle());
        mv.addObject("userFirstname", currentUser.getForename());
        mv.addObject("userLastname", currentUser.getSurname());
        mv.addObject("userEmail", currentUser.getEmail());
        mv.addObject("userOrganisation", currentUser.getOrganisation().getName());

        return mv;
    }

    @PostMapping("/profile")
    public ModelAndView changeUserAccount(@ModelAttribute("user") UserDTO userDTO,
                                          BindingResult result) {
        ModelAndView mv = new ModelAndView();

        if (result.hasErrors()) {
            mv.setViewName("profile");

            mv.addObject("user", userDTO);
            mv.addObject("error", result);

            return mv;
        } else {
            User user = null;
            try {
                userService.changeUserAccount(userDTO);
            } catch (UserService.UserAlreadyExistsException e) {
                mv.addObject("user", userDTO);
                mv.addObject("error", result);

                return mv;
            }


            mv.setViewName("profile");

            return mv;
        }
    }

}
