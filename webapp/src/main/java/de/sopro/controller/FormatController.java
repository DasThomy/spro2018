package de.sopro.controller;

import de.sopro.DTO.FormatDTO;
import de.sopro.DTO.VersionDTO;
import de.sopro.model.Format;
import de.sopro.repository.FormatRepository;
import de.sopro.services.FormatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.naming.NameAlreadyBoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

import static java.lang.Integer.parseInt;

@Controller
public class FormatController {

    @Autowired
    FormatService formatService;

    @Autowired
    FormatRepository formatRepository;

    @GetMapping("/formats/new")
    public ModelAndView createFormatView() {
        ModelAndView mv = new ModelAndView("newformat");

        FormatDTO formatDTO = new FormatDTO();
        mv.addObject("format", formatDTO);

        return mv;
    }

    @PostMapping("/formats/new")
    public ModelAndView createFormat(@ModelAttribute("format") @Valid FormatDTO formatDTO,
                                            BindingResult result) {
        ModelAndView mv = new ModelAndView();

        if(result.hasErrors()) {
            mv.setViewName("createFormats");

            mv.addObject("user", formatDTO);
            mv.addObject("error", result);

            return mv;
        } else {

            try {
                formatService.createFormat(formatDTO);
            } catch (NameAlreadyBoundException e) {
                mv.setViewName("registration");

                mv.addObject("user", formatDTO);
                mv.addObject("errorMsg", "one Version can only be once in a Format");

                return mv;
            }

            String redirect = "redirect:/formats/";
            mv.setViewName(redirect);

            return mv;
        }
    }

    @GetMapping(value = "/formats")
    public ModelAndView formatListView() {

        //todo format list template name
        ModelAndView mv = new ModelAndView("formats");
        List<Format> formats = formatRepository.findAll();

        mv.addObject("formats", formats);
        return mv;
    }

    @GetMapping("/formats/{format}")
    public ModelAndView editFormatView(@PathVariable String format) {
        ModelAndView mv = new ModelAndView("editFormat");
        VersionDTO versionDTO = new VersionDTO();
        mv.addObject("format", formatRepository.findFormatById(parseInt(format)).get());
        mv.addObject("versionDTO", versionDTO);
        return mv;
    }

    @PostMapping("/formats/{format}")
    public ModelAndView editFormat(@PathVariable("format") String format, @ModelAttribute("versions") String versions,
                                     BindingResult result) {
        ModelAndView mv = new ModelAndView();
        if(result.hasErrors()) {
            mv.setViewName("editFormat");

            mv.addObject("error", result);

            return mv;
        } else {

            formatService.addVersion(formatRepository.findFormatById(parseInt(format)).get(), versions);


            String redirect = "redirect:/formats/";
            mv.setViewName(redirect);

            return mv;
        }
    }

    @GetMapping("/formats/delete/{formatID}")
    public ModelAndView deleteFormat(@PathVariable("formatID") int formatID) {
        ModelAndView mv = new ModelAndView();

        try{
            formatService.deleteFormat(formatID);
        } catch (IllegalStateException e) {
            mv.setStatus(HttpStatus.CONFLICT);
            mv.setViewName("error");
            return mv;
        } catch(NoSuchElementException e){
            mv.setStatus(HttpStatus.NOT_FOUND);
            mv.setViewName("error");
            return mv;
        }

        mv.setViewName("redirect:/formats/");

        return mv;
    }
}
