package de.sopro.controller;

import com.fasterxml.jackson.annotation.JsonView;
import de.sopro.exceptions.NoAccessToRessourceException;
import de.sopro.filter.Views;
import de.sopro.model.*;
import de.sopro.repository.*;
import de.sopro.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
//todo delete homecontroller
@Controller
public class HomeController {

    private final Logger log = LoggerFactory.getLogger(CombinationController.class);

    @Autowired
    ProductRepository repo;

    @Autowired
    CombinationRepository combinationRepository;

	@Autowired
    TagRepository tagRepository;

	@Autowired
    ProductRepository productRepository;

    @Autowired
    FormatVersionRepository formatVersionRepository;

    @Autowired
    FormatRepository formatRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SecurityService securityService;

	// Note that this value is overridden via injection from
	// file application.properties
	@Value("${home.welcome}")
	String message = "dummy";

    @RequestMapping("/printUser")
    public String test(Principal principal) {
        Optional<User> user = securityService.getLoggedInUser();

        System.out.println(user);

        return "home";
    }

    @JsonView(Views.BasicProduct.class)
    @ResponseBody
    @RequestMapping("/basicproduct")
    public Product basicProduct() {

        return productRepository.findAll().get(0);
    }
    @JsonView(Views.DetailProduct.class)
    @ResponseBody
    @RequestMapping("/detailproduct")
    public Product detailProduct() {

        return productRepository.findAll().get(0);
    }
    @JsonView(Views.BasicCombination.class)
    @ResponseBody
    @RequestMapping("/basiccombination")
    public Combination BasicCombination() {

        return combinationRepository.findAll().get(0);
    }

    @JsonView(Views.DetailCombination.class)
    @ResponseBody
    @RequestMapping("/detailcombination")
    public Combination DetailCombination() {
        return combinationRepository.findAll().get(0);
    }

    @JsonView(Views.BasicFormat.class)
    @ResponseBody
    @RequestMapping("/basicformat")
    public Format BasicFormat() {
        return formatRepository.findAll().get(0);
    }
    @JsonView(Views.DetailFormat.class)
    @ResponseBody
    @RequestMapping("/detailformat")
    public Format DetailFormat() {

        return formatRepository.findAll().get(0);
    }
    @JsonView(Views.BasicFormatVersion.class)
    @ResponseBody
    @RequestMapping("/basicformatversion")
    public FormatVersion BasicFormatVersion() {

        return formatVersionRepository.findAll().get(0);
    }
    @JsonView(Views.DetailFormatVersion.class)
    @ResponseBody
    @RequestMapping("/detailformatversion")
    public FormatVersion DetailFormatVersion() {

        return formatVersionRepository.findAll().get(0);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView home() {
        ModelAndView mv = new ModelAndView("home");
        mv.addObject("products", productRepository.findAll());
        return mv;
    }


    @JsonView(Views.BasicCombination.class)
    @ResponseBody
    @RequestMapping("/sharedComb")
    public List<Combination> sharedCombination(Principal principal) {
        if(principal.getName() == null){
            log.info("not logged in");
            throw new NoAccessToRessourceException();
        }
        User user = userRepository.findByEmail(principal.getName()).get();
        return user.getSharedCombinations();
    }

    @JsonView(Views.BasicCombination.class)
    @ResponseBody
    @RequestMapping("/ownComb")
    public List<Combination> ownCombination(Principal principal) {
        if(principal.getName() == null){
            log.info("not logged in");
            throw new NoAccessToRessourceException();
        }
        User user = userRepository.findByEmail(principal.getName()).get();
        return user.getOwnCombinations();
    }

}
