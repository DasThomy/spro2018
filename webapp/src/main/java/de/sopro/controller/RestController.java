package de.sopro.controller;

import com.fasterxml.jackson.annotation.JsonView;
import de.sopro.filter.Views;
import de.sopro.model.Combination;
import de.sopro.model.Product;
import de.sopro.services.CombinationService;
import de.sopro.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@RequestMapping("/app")
@org.springframework.web.bind.annotation.RestController
public class RestController {

    private final Logger logger = LoggerFactory.getLogger(RestController.class);

    //Used Services
    private final CombinationService combinationService;
    private final ProductService productService;

    @Autowired
    public RestController(CombinationService combinationService, ProductService productService) {
        this.combinationService = combinationService;
        this.productService = productService;
    }

    @GetMapping("/login")
    public HttpStatus login() {
        return HttpStatus.ACCEPTED;
    }

    @JsonView(Views.DetailCombination.class)
    @GetMapping("/combinations/{id}")
    public ResponseEntity<Combination> getCombination(@PathVariable int id) {
        Optional<Combination> optionalCombination = combinationService.getCombination(id);

        return optionalCombination
                .map(combination -> new ResponseEntity<>(combination, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @JsonView(Views.BasicCombination.class)
    @GetMapping("/combinations/own")
    public ResponseEntity<List<Combination>> getOwnCombinations() {
        return new ResponseEntity<>(combinationService.getOwnCombinations(), HttpStatus.OK);
    }

    @JsonView(Views.BasicCombination.class)
    @GetMapping("/combinations/shared")
    public ResponseEntity<List<Combination>> getSharedCombinations() {
        return new ResponseEntity<>(combinationService.getSharedCombinations(), HttpStatus.OK);
    }

    @JsonView(Views.BasicCombination.class)
    @GetMapping("/combinations/public")
    public ResponseEntity<List<Combination>> getPublicCombinations() {
        return new ResponseEntity<>(combinationService.getPublicCombinations(), HttpStatus.OK);
    }

    @JsonView(Views.DetailProduct.class)
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable int id) {
        Optional<Product> optionalProduct = productService.getProduct(id);

        return optionalProduct
                .map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/products/{id}/logo")
    public ResponseEntity<byte[]> getProductLogo(@PathVariable int id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @JsonView(Views.BasicProduct.class)
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    @JsonView(Views.BasicProduct.class)
    @GetMapping(value = "/products", params = {"search", "onlyCertified"})
    public ResponseEntity<List<Product>> searchProduct(@RequestParam String search, @RequestParam boolean onlyCertified) {
        return new ResponseEntity<>(productService.searchProduct(search, onlyCertified), HttpStatus.OK);
    }

    @JsonView(Views.BasicCombination.class)
    @GetMapping(value = "/combinations", params = {"search"})
    public ResponseEntity<CombinationService.SearchResult> searchCombination(@RequestParam String search) {
        return new ResponseEntity<>(combinationService.searchFor(search),HttpStatus.OK);
    }
}
