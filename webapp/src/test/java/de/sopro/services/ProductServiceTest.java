package de.sopro.services;

import de.sopro.model.Product;
import de.sopro.model.Tag;
import de.sopro.repository.ProductRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductServiceTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductService productService;

    private Product product;

    @Transactional
    @BeforeAll
    void init(){
        Tag tag1 = new Tag();
        tag1.setName("someTag");

        product = new Product();
        product.setName("something");
        product.addTag(tag1);
        productRepository.save(product);
    }

    @Test
    void searchNotExistingProduct() {
        List<Product> searchResult = productService.searchProduct("different", false);
        assertThat(searchResult).isEmpty();
    }


    @Test
    void searchProductByTag() {
        List<Product> searchResult = productService.searchProduct("someTag", false);
        assertThat(searchResult).contains(product);
    }


    @Test
    void searchProductByName() {
        List<Product> searchResult = productService.searchProduct("thing", false);
        assertThat(searchResult).contains(product);
    }


    @Test
    void searchProductByTagAndName() {
        List<Product> searchResult = productService.searchProduct("thing meTag", false);
        assertThat(searchResult).contains(product);
    }


    @Test
    void searchProductByWrongTagAndName() {
        List<Product> searchResult = productService.searchProduct("something different", false);
        assertThat(searchResult).isEmpty();
    }


    @Test
    void searchProductByTagAndWrongName() {
        List<Product> searchResult = productService.searchProduct("sometag different", false);
        assertThat(searchResult).isEmpty();
    }


    @Test
    void searchProductByUppercase() {
        List<Product> searchResult = productService.searchProduct("SOMETHING SOMETAG", false);
        assertThat(searchResult).contains(product);
    }

}