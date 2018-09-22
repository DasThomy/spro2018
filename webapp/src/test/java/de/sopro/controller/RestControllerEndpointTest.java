package de.sopro.controller;

import de.sopro.model.Combination;
import de.sopro.model.Product;
import de.sopro.services.CombinationService;
import de.sopro.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class RestControllerEndpointTest {

    private MockMvc mockMvc;
    private CombinationService combinationServiceMock;
    private ProductService productServiceMock;
    private RestController restController;

    @BeforeEach
    public void setUp() {
        combinationServiceMock = mock(CombinationService.class);
        productServiceMock = mock(ProductService.class);
        restController = new RestController(combinationServiceMock, productServiceMock);
        mockMvc = MockMvcBuilders.standaloneSetup(restController).build();
    }

    @Test
    public void testGetSingleCombinationEndpoint() throws Exception {
        when(combinationServiceMock.getCombination(anyInt())).thenReturn(Optional.of(new Combination()));

        mockMvc.perform(get("/app/combinations/5")).
                andExpect(status().isOk());
    }

    @Test
    public void testGetCombinationsOwnEndpoint() throws Exception {
        when(combinationServiceMock.getOwnCombinations()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/app/combinations/own")).
                andExpect(status().isOk());
    }

    @Test
    public void testGetCombinationsSharedEndpoint() throws Exception {
        when(combinationServiceMock.getSharedCombinations()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/app/combinations/shared")).
                andExpect(status().isOk());
    }

    @Test
    public void testGetCombinationsPublicEndpoint() throws Exception {
        when(combinationServiceMock.getSharedCombinations()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/app/combinations/public")).
                andExpect(status().isOk());
    }

    @Test
    public void testGetSingleProductEndpoint() throws Exception {
        when(productServiceMock.getProduct(anyInt())).thenReturn(Optional.of(new Product()));

        mockMvc.perform(get("/app/products/5")).
                andExpect(status().isOk());
    }

    @Test
    public void testGetAllProductsEndpoint() throws Exception {
        when(combinationServiceMock.getOwnCombinations()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/app/products")).
                andExpect(status().isOk());
    }
}