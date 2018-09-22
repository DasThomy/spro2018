package de.sopro.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sopro.filter.Views;
import de.sopro.model.Combination;
import de.sopro.model.User;
import de.sopro.repository.CombinationRepository;
import de.sopro.repository.ProductRepository;
import de.sopro.security.SecurityService;
import de.sopro.services.CombinationService;
import javassist.tools.rmi.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequestMapping("/combinations")
@Controller
public class CombinationController {

    private final Logger logger = LoggerFactory.getLogger(CombinationController.class);

    //Used Services
    private final CombinationService combinationService;
    private final SecurityService securityService;
    private ProductRepository productRepository;

    @Autowired
    public CombinationController(CombinationService combinationService, SecurityService securityService) {
        this.combinationService = combinationService;
        this.securityService = securityService;
    }


    @JsonView(Views.DetailCombination.class)
    @GetMapping(value = "/{combID:[0-9]+}")
    public ModelAndView combinationView(@PathVariable int combID) throws JsonProcessingException {
        logger.info("Combination '" + combID + "' requested.");

        ModelAndView mv = new ModelAndView();

        Optional<Combination> comb = combinationService.getCombination(combID);
        Optional<User> user = securityService.getLoggedInUser();

        if (comb.isPresent()) {
            if (!user.isPresent() && !comb.get().isPublicVisible()) {
                logger.warn("Not logged in, but combination is not public");
                mv.setViewName("login");
                return mv;
            } else if (user.isPresent() && !combinationService.isVisibleFor(user.get(), comb.get())) {
                logger.warn("User has no access to combination");
                mv.setViewName("home");
                return mv;
            }

            mv.setViewName("home");

            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
            String jsonInString = mapper.writerWithView(Views.DetailCombination.class).writeValueAsString(comb.get());

            mv.addObject("comb", jsonInString);
            mv.addObject("combination", comb.get());
        } else {
            logger.warn("Combination '" + combID + "' not found");
            mv.setViewName("home");
        }

        return mv;
    }

    @JsonView(Views.BasicCombination.class)
    @GetMapping(value = "")
    public ModelAndView combinationListView() {

        ModelAndView mv = new ModelAndView("combinationList");

        mv.addObject("public", combinationService.getPublicCombinations());
        mv.addObject("shared", combinationService.getSharedCombinations());
        mv.addObject("own", combinationService.getOwnCombinations());
        logger.info("All public, shared and own combinations requested");

        return mv;
    }

    @GetMapping(value = "/new")
    public ModelAndView createCombinationView() {

        logger.info("createCombinationView requested");

        return new ModelAndView("createCombination");
    }


    @PostMapping(value = "")
    public ModelAndView createCombination(@RequestBody String newCombination) {
        ModelAndView mv;
        try {

            Combination comb = combinationService.createCombination(newCombination);

            String redirect = "redirect:/combinations/" + comb.getId();

            logger.info("created the combination \"" + comb.getName() + "\" and redirected to \"" + redirect + "\"");

            mv = new ModelAndView(redirect);
        } catch (AuthenticationException e) {
            logger.info("User not logged in");
            mv = new ModelAndView("error");
        } catch (IOException e) {
            logger.info("Could not parse JSON");
            e.printStackTrace();
            mv = new ModelAndView("error");
        } catch (ObjectNotFoundException e) {
            logger.info("Could not parse JSON");
            e.printStackTrace();
            mv = new ModelAndView("error");
        }
        return mv;
    }

    @PutMapping(value = "/{combID:[0-9]+}")
    public ModelAndView saveCombination(@RequestBody String newCombination, @PathVariable(value = "combID") int combID) {
        ModelAndView mv = new ModelAndView();

        Optional<Combination> optionalComb = combinationService.getCombination(combID);

        logger.info("Save combination '" + combID + "' requested");

        if (!optionalComb.isPresent()) {
            logger.warn("Combination '" + combID + "' not found -> could not be saved");
            mv.setViewName("error");
            mv.setStatus(HttpStatus.NOT_FOUND);
            return mv;
        }
        try {
            combinationService.saveCombination(newCombination, combID);
            String redirect = "redirect:/combinations/" + combID;
            logger.info("saved the combination \"" + combID + "\" and redirected to \"" + redirect + "\"");
            mv.setViewName(redirect);

        } catch (ObjectNotFoundException e) {
            mv.setViewName("error");
            mv.setStatus(HttpStatus.NOT_FOUND);

        } catch (IOException e) {
            mv.setViewName("error");
            mv.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return mv;
    }



    @JsonView(Views.BasicCombination.class)
    @ResponseBody
    @GetMapping(value = "", params = {"search"})
    public CombinationService.SearchResult searchCombinations(@RequestParam String search) {
        return combinationService.searchFor(search);
    }

    @DeleteMapping("/{combID}")
    public ModelAndView deleteCombination(@PathVariable("combID") int combID) {
        ModelAndView mv = new ModelAndView();

        try {
            combinationService.deleteCombination(combID);
        } catch (AuthenticationException e) {
            mv.setStatus(HttpStatus.UNAUTHORIZED);
            return mv;
        } catch(NoSuchElementException e){
            mv.setStatus(HttpStatus.NOT_FOUND);
            return mv;
        }

        mv.setViewName("redirect:/combinations/");

        return mv;
    }
}
