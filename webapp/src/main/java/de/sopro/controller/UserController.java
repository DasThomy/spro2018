package de.sopro.controller;

import de.sopro.model.User;
import de.sopro.services.UserService;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(CombinationController.class);

    //Used services
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "")
    public ModelAndView loadUser(@RequestParam(value = "search", required = false) String search) {
        logger.info("Users are shown");

        ModelAndView mv = new ModelAndView("user");
        List<User> user = (search == null) ? userService.getAllUser() : userService.getUsersByEMailSearch(search);
        mv.addObject("users", user);

        return mv;
    }

    @ResponseBody
    @PostMapping(value = "/admin")
    public ResponseEntity makeAdministrator(@RequestBody String jsonUser) {
        logger.info("Do/undo a user to an administrator.");

        try {
            userService.makeAdministrator(jsonUser);
        } catch (IOException | NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }


}
