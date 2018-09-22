package de.sopro.controller;

import com.fasterxml.jackson.annotation.JsonView;
import de.sopro.DTO.ProductDTO;
import de.sopro.filter.Views;
import de.sopro.logic.AlternativeProducts;
import de.sopro.logic.CompatibilityLogic;
import de.sopro.model.Compatibility;
import de.sopro.model.Product;
import de.sopro.repository.FormatRepository;
import de.sopro.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@Controller
public class ProductController {

    private final Logger logger = LoggerFactory.getLogger(ProductController.class);

    //Used repositories
    private final FormatRepository formatRepository;

    //Used services
    private final ProductService productService;


    @Autowired
    public ProductController(ProductService productService, FormatRepository formatRepository) {
        this.productService = productService;
        this.formatRepository = formatRepository;
    }


    @JsonView(Views.DetailProduct.class)
    @GetMapping(value = "/products")
    public ModelAndView searchProduct(@RequestParam(value = "search", required = false) String search,
                                      @RequestParam(value = "onlyCertified", required = false) boolean onlyCertified) {

        ModelAndView mv = new ModelAndView("products");

        List<Product> products = productService.getProductList(search, onlyCertified);

        mv.addObject("products", products);
        return mv;
    }

    // this view matches '/product-list' and 'combinations/ID/product-list'
    @JsonView(Views.DetailProduct.class)
    @GetMapping(value = "/**/product-list")
    public ModelAndView loadProducts(@RequestParam(value = "search", required = false) String search,
                                     @RequestParam(value = "onlyCertified", required = false) boolean onlyCertified) {

        ModelAndView mv = new ModelAndView("fragments/productList :: #productList");

        List<Product> products = productService.getProductList(search, onlyCertified);

        mv.addObject("products", products);
        return mv;
    }

    @GetMapping("/products/compatibility/{prodID1:[0-9]+}/{prodID2:[0-9]+}")
    public ResponseEntity<Compatibility> checkCompatibility(@PathVariable(value = "prodID1") int prodID1,
                                                            @PathVariable(value = "prodID2") int prodID2) {

        Optional<Product> optionalProduct1 = productService.getProduct(prodID1);
        Optional<Product> optionalProduct2 = productService.getProduct(prodID2);

        if (!optionalProduct1.isPresent() || !optionalProduct2.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Compatibility compatibility = CompatibilityLogic.getInstance()
                .getCompatibility(optionalProduct1.get(), optionalProduct2.get());
        return new ResponseEntity<>(compatibility, HttpStatus.OK);
    }

    @JsonView(Views.BasicProduct.class)
    @GetMapping("/products/alternatives/{prodID1:[0-9]+}/{prodID2:[0-9]+}")
    public ResponseEntity<List<AlternativeProducts>> getAlternatives(@PathVariable(value = "prodID1") int prodID1,
                                                                     @PathVariable(value = "prodID2") int prodID2) {

        Optional<Product> optionalProduct1 = productService.getProduct(prodID1);
        Optional<Product> optionalProduct2 = productService.getProduct(prodID2);

        if (!optionalProduct1.isPresent() || !optionalProduct2.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<AlternativeProducts> alternatives;
        try {
            alternatives = CompatibilityLogic.getInstance()
                    .getAlternativeProducts(optionalProduct1.get(), optionalProduct2.get());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(alternatives, HttpStatus.OK);
    }

    @RequestMapping("/products/{prodID:[0-9]+}")
    public ModelAndView editProduct(@PathVariable(value = "prodID") int prodID) {
        ModelAndView mv = new ModelAndView("editProduct");

        Optional<Product> optionalProduct = productService.getProduct(prodID);

        if (!optionalProduct.isPresent()) {
            mv.setStatus(HttpStatus.NOT_FOUND);
        }
        mv.addObject("logo", optionalProduct.get().getPicture());

        mv.addObject("product", optionalProduct.get());
        mv.addObject("formats", formatRepository.findAll());

        return mv;
    }

    @GetMapping("/products/new")
    public ModelAndView createProductView() {
        ModelAndView mv = new ModelAndView("newProduct");

        ProductDTO productDTO = new ProductDTO();

        mv.addObject("Product", productDTO);
        mv.addObject("formats", formatRepository.findAll());

        return mv;
    }

    @PostMapping("/products/new")
    public ModelAndView createProduct(@ModelAttribute("product")
                                      @Valid ProductDTO productDTO,
                                      BindingResult result) {
        ModelAndView mv = new ModelAndView();


        if (result.hasErrors()) {
            mv.setViewName("newProduct");

            mv.addObject("user", productDTO);
            mv.addObject("error", result);

            return mv;
        } else {


            Product product = productService.createProduct(productDTO);
            mv.setViewName("redirect:/products/new/" + product.getId() + "/logo");
            mv.addObject("product", product.getId());
            return mv;

        }
    }

    @GetMapping("/products/upload")
    public ModelAndView uploadPage() {
        return new ModelAndView("uploadProducts");
    }


    @PostMapping("/products/upload")
    public ModelAndView uploadProducts(@RequestParam("files") MultipartFile[] files) {
        ModelAndView mv = new ModelAndView("redirect:/products");

        List<String> jsonFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                jsonFiles.add(new String(file.getBytes()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        List<Product> createdProducts = new ArrayList<>();

        for (String jsonFile : jsonFiles) {
            try {
                createdProducts.addAll(productService.uploadProducts(jsonFile));
            } catch (Exception e) {
                logger.info(e.getMessage());
                mv.addObject("error", "Daten sind ungültig");
            }
        }

        mv.addObject("products", createdProducts);

        return mv;
    }

    @PostMapping("/products/upload/logos")
    public ModelAndView uploadLogos(@RequestParam("logos") MultipartFile[] files) {
        ModelAndView mv = new ModelAndView("redirect:/products");

        productService.addLogos(files);

        return mv;
    }


    @GetMapping("/products/delete/{productID}")
    public ModelAndView deleteProduct(@PathVariable("productID") int productID) {
        ModelAndView mv = new ModelAndView();

        try {
            productService.deleteProduct(productID);
        } catch (IllegalStateException e) {
            mv.setStatus(HttpStatus.CONFLICT);
            mv.setViewName("error");
            return mv;
        } catch (NoSuchElementException e) {
            mv.setStatus(HttpStatus.NOT_FOUND);
            mv.setViewName("error");
            return mv;
        }

        mv.setViewName("redirect:/products");

        return mv;
    }

    @PostMapping("/products/{prodID:[0-9]+}/logo")
    public ModelAndView uploadLogo(@RequestParam("logos") MultipartFile[] files,
                                   @PathVariable(value = "prodID") int prodID) {
        ModelAndView mv = new ModelAndView("redirect:/products");

        if(files.length > 0) {
            productService.addLogoToProduct(files[0], prodID);
        }

        return mv;
    }


    @GetMapping("/products/new/{prodID:[0-9]+}/logo")
    public ModelAndView uploadLogoToNewProduct(@PathVariable(value = "prodID") int prodID) {
        logger.info("uploadLogoToNewProduct");

        ModelAndView mv = new ModelAndView("uploadLogo");
        mv.addObject("product", prodID);
        return mv;

    }

    @PostMapping("/products/new/{prodID:[0-9]+}/logo")
    public ModelAndView uploadLogoToProduct(@RequestParam("logos") MultipartFile[] files,
                                            @PathVariable(value = "prodID") int prodID) {
        ModelAndView mv = new ModelAndView("redirect:/products");

        if(files.length > 0) {
            productService.addLogoToProduct(files[0], prodID);
        }

        return mv;
    }

}
