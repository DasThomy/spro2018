package de.sopro.controller;

import de.sopro.model.User;
import de.sopro.security.SecurityService;
import de.sopro.DTO.UserDTO;
import de.sopro.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class LoginController {

    private final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final UserService userService;
    private final SecurityService securityService;

    @Autowired
    public LoginController(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }


    @GetMapping("/login")
    public ModelAndView login(String error, String logout) {
        ModelAndView mv = new ModelAndView("login");

        if (error != null) {
            mv.addObject("error", "Your username and password is invalid.");
        }


        if (logout != null) {
            mv.addObject("message", "You have been logged out successfully.");
        }

        return mv;
    }


    @GetMapping("/registration")
    public ModelAndView showRegistration() {
        ModelAndView mv = new ModelAndView("registration");

        UserDTO userDTO = new UserDTO();
        mv.addObject("user", userDTO);

        return mv;
    }

    @PostMapping("/registration")
    public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid UserDTO userDTO,
                                            BindingResult result) {
        ModelAndView mv = new ModelAndView();

        if(result.hasErrors()) {
            mv.setViewName("registration");

            mv.addObject("user", userDTO);
            mv.addObject("error", result);

            return mv;
        } else {
            User user;
            try {
                user = userService.registerNewUserAccount(userDTO);
            } catch (UserService.UserAlreadyExistsException e) {
                result.rejectValue("email", "message.regError", "E-Mail existiert bereits");
                mv.addObject("user", userDTO);
                mv.addObject("error", result);

                return mv;
            }

            securityService.autoLogin(user);

            mv.setViewName("home");

            return mv;
        }
    }

}
