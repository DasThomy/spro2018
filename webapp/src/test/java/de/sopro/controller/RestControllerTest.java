package de.sopro.controller;

import de.sopro.model.Combination;
import de.sopro.model.Product;
import de.sopro.services.CombinationService;
import de.sopro.services.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RestControllerTest {

    private CombinationService combinationServiceMock;
    private ProductService productServiceMock;
    private RestController restController;

    @BeforeEach
    public void setUp() {
        combinationServiceMock = mock(CombinationService.class);
        productServiceMock = mock(ProductService.class);
        restController = new RestController(combinationServiceMock, productServiceMock);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetCombinationExisting() {
        Combination expectedCombination = new Combination();
        when(combinationServiceMock.getCombination(anyInt())).thenReturn(Optional.of(expectedCombination));

        ResponseEntity<Combination> response = restController.getCombination(anyInt());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedCombination);
        verify(combinationServiceMock, times(1)).getCombination(anyInt());
    }

    @Test
    void testGetCombinationNotExisting() {
        when(combinationServiceMock.getCombination(anyInt())).thenReturn(Optional.empty());

        ResponseEntity<Combination> response = restController.getCombination(anyInt());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(combinationServiceMock, times(1)).getCombination(anyInt());
    }

    @Test
    void testGetOwnCombinations() {
        List<Combination> expectedCombinations = new ArrayList<>();
        expectedCombinations.add(new Combination());
        expectedCombinations.add(new Combination());
        when(combinationServiceMock.getOwnCombinations()).thenReturn(expectedCombinations);

        ResponseEntity<List<Combination>> response = restController.getOwnCombinations();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedCombinations);
        verify(combinationServiceMock, times(1)).getOwnCombinations();
    }

    @Test
    void testGetSharedCombinations() {
        List<Combination> expectedCombinations = new ArrayList<>();
        expectedCombinations.add(new Combination());
        expectedCombinations.add(new Combination());
        when(combinationServiceMock.getSharedCombinations()).thenReturn(expectedCombinations);

        ResponseEntity<List<Combination>> response = restController.getSharedCombinations();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedCombinations);
        verify(combinationServiceMock, times(1)).getSharedCombinations();
    }

    @Test
    void testGetPublicCombinations() {
        List<Combination> expectedCombinations = new ArrayList<>();
        expectedCombinations.add(new Combination());
        expectedCombinations.add(new Combination());
        when(combinationServiceMock.getPublicCombinations()).thenReturn(expectedCombinations);

        ResponseEntity<List<Combination>> response = restController.getPublicCombinations();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedCombinations);
        verify(combinationServiceMock, times(1)).getPublicCombinations();
    }

    @Test
    void testGetProductExisting() {
        Product expectedProduct = new Product();
        when(productServiceMock.getProduct(anyInt())).thenReturn(Optional.of(expectedProduct));

        ResponseEntity<Product> response = restController.getProduct(anyInt());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedProduct);
        verify(productServiceMock, times(1)).getProduct(anyInt());
    }

    @Test
    void testGetProductNotExisting() {
        when(productServiceMock.getProduct(anyInt())).thenReturn(Optional.empty());

        ResponseEntity<Product> response = restController.getProduct(anyInt());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(productServiceMock, times(1)).getProduct(anyInt());
    }

    //TODO: Implement test for getting Logos
//    @Test
//    void testGetProductLogo() {
//    }

    @Test
    void testGetAllProducts() {
        List<Product> expectedProducts = new ArrayList<>();
        expectedProducts.add(new Product());
        expectedProducts.add(new Product());
        when(productServiceMock.getAllProducts()).thenReturn(expectedProducts);

        ResponseEntity<List<Product>> response = restController.getAllProducts();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedProducts);
        verify(productServiceMock, times(1)).getAllProducts();
    }
}