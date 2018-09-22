package de.sopro.controller;

import de.sopro.model.Product;
import de.sopro.services.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//TODO: Implement nicely


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productServiceMock;

//    @Test
//    public void testGetAllProductsEndpoint() throws Exception {
//        when(productServiceMock.getAllProducts()).thenReturn(new ArrayList<>());
//
//        mockMvc.perform(get("/products")).andExpect(status().isOk());
//    }
//
//
//    @Test
//    public void testGetAllProductsSearchEndpoint() throws Exception {
//        when(productServiceMock.searchProduct(anyString(), anyBoolean())).thenReturn(new ArrayList<>());
//
//        mockMvc.perform(get("/products?search=abc")).
//                andExpect(status().isOk());
//    }
//
//    @Test
//    public void testGetCompabilityEndpoint() throws Exception {
//        when(productServiceMock.getProduct(anyInt())).thenReturn(Optional.of(new Product()));
//
//        mockMvc.perform(get("/products/compatibility/" + 5 + "/" + 6)).
//                andExpect(status().isOk());
//    }

    //todo look at this
    /*
    @Test
    public void testGetAlternativesEndpoint() throws Exception {
        when(productServiceMock.getProduct(anyInt())).thenReturn(Optional.of(new Product()));

        mockMvc.perform(get("/products/alternatives/" + 5 + "/" + 6)).andExpect(status().isBadRequest());
    }
    */

    //TODO Upload url endpoint test
//    @Test
//    public void testUploadEndpoint() throws Exception {
//        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/products/upload");
//        request.contentType(MediaType.APPLICATION_JSON);
//
//        mockMvc.perform(request).andExpect(status().is3xxRedirection());
//    }
}