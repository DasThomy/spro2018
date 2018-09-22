package de.sopro.services;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sopro.filter.Views;
import de.sopro.model.*;
import de.sopro.repository.*;

import javassist.tools.rmi.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class CombinationServicesTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    CombinationRepository combinationRepository;

    @Autowired
    CombinationService combinationService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductInCombRepository productInCombRepository;

    @Autowired
    ConnectionRepository connectionRepository;

    @Test
    void saveNotExistingCombination() throws Exception{
        User user = new User("a", "a", "a");
        Combination combination = user.addOwnCombination();

        //make sure id 1 doesn't exist
        if(combinationRepository.findById(1).isPresent()){
            combinationRepository.delete(combinationRepository.findById(1).get());
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        String jsonInString = mapper.writerWithView(Views.DetailCombination.class).writeValueAsString(combination);

        assertThatThrownBy(() -> {
            combinationService.saveCombination(jsonInString, 1);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Transactional
    void saveNotReadableCombination() {
        User user = userRepository.save(new User ("test","test", "test"));
        Combination comb = user.addOwnCombination();

        combinationRepository.save(comb);

        assertThatThrownBy(() -> {
            combinationService.saveCombination("---", comb.getId());
        }).isInstanceOf(IOException.class);

    }


    @Test
    @Transactional
    void saveCombination() throws Exception{
        User user = userRepository.save(new User ("test","test", "test"));
        Combination comb = user.addOwnCombination();

        Product product1 = new Product();
        Product product2 = new Product();

        ProductInComb productInComb1 = comb.addProductInComb(product1);
        ProductInComb productInComb2 = comb.addProductInComb(product2);

        comb.addConnection(productInComb1, productInComb2);

        assertThat(comb.getConnections()).isNotEmpty();
        assertThat(comb.getConnections()).isNotEmpty();

        combinationRepository.save(comb);

        String jsonComb  = "{\"id\":276,\"name\":\"testcomb\",\"publicVisible\":true,\"connections\":[],\"productsInComb\":[]}";

        combinationService.saveCombination(jsonComb, comb.getId());

        Combination combination = combinationRepository.findById(comb.getId()).get();
        assertThat(combination.getName()).isEqualTo(new String("testcomb"));
        assertThat(combination.isPublicVisible()).isTrue();
        assertThat(combination.getConnections()).isEmpty();
        assertThat(combination.getConnections()).isEmpty();

    }

    @Test
    @Transactional
    void saveCombinationWithConnectionWhenProductsDontExist() {
        User user = userRepository.save(new User ("test","test", "test"));
        Combination comb = user.addOwnCombination();

        combinationRepository.save(comb);

        String jsonComb  = "{\"id\":820,\"name\":\"comb0\",\"publicVisible\":true,\"connections\":[{\"id\":851,\"sourceProduct\":833,\"targetProduct\":844,\"compatibility\":null},{\"id\":852,\"sourceProduct\":844,\"targetProduct\":833,\"compatibility\":null},{\"id\":858,\"sourceProduct\":838,\"targetProduct\":833,\"compatibility\":null},{\"id\":860,\"sourceProduct\":838,\"targetProduct\":844,\"compatibility\":null}],\"productsInComb\":[{\"id\":833,\"xPosition\":494,\"yPosition\":355,\"product\":{\"id\":815,\"name\":\"product5\"}},{\"id\":838,\"xPosition\":403,\"yPosition\":74,\"product\":{\"id\":819,\"name\":\"product9\"}},{\"id\":844,\"xPosition\":55,\"yPosition\":415,\"product\":{\"id\":810,\"name\":\"product0\"}}]}";


        assertThatThrownBy(() -> {
            combinationService.saveCombination(jsonComb, comb.getId());
        }).isInstanceOf(ObjectNotFoundException.class);

    }

    @Test
    @Transactional
    void saveCombinationWithProducts() throws Exception{
        User user = userRepository.save(new User ("test","test", "test"));
        Combination comb = user.addOwnCombination();

        Product product1 = new Product();

        combinationRepository.save(comb);

        product1 = productRepository.findAll().get(0);

        String jsonComb  = "{\"id\":820,\"name\":\"comb0\",\"publicVisible\":true,\"connections\":[],\"productsInComb\":[{\"id\":844,\"xPosition\":55,\"yPosition\":415,\"product\":{\"id\":" + product1.getId() + ",\"name\":\"product0\"}}]}";

        combinationService.saveCombination(jsonComb, comb.getId());
    }

    @Test
    @Transactional
    void saveCombinationWithConnections() throws Exception{
        Product product1 = new Product();
        Product product2 = new Product();
        productRepository.save(product1);
        productRepository.save(product2);


        User user = new User ("test","test", "test");
        user.setPassword("j");
        userRepository.save(user);

        Combination previousComb = user.addOwnCombination();
        combinationRepository.save(previousComb);

        Combination comb = user.addOwnCombination();

        ProductInComb productInComb1 = comb.addProductInComb(product1);
        ProductInComb productInComb2 = comb.addProductInComb(product2);
        comb.addConnection(productInComb1, productInComb2);
        //save to generate ids
        combinationRepository.save(comb);


        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        String jsonInString = mapper.writerWithView(Views.DetailCombination.class).writeValueAsString(comb);

        combinationService.saveCombination(jsonInString, previousComb.getId());
    }

    @Test
    @Transactional
    public void amountOfConnectionsSameAfterSaving() throws Exception{
        Product product = new Product();
        productRepository.save(product);

        User user = new User ("test","test", "test");
        user.setPassword("j");
        userRepository.save(user);

        Combination comb = user.addOwnCombination();
        comb.setName("name");
        ProductInComb productInComb1 = comb.addProductInComb(product);
        ProductInComb productInComb2 = comb.addProductInComb(product);

        comb.addConnection(productInComb1, productInComb2);
        combinationRepository.save(comb);

        long connectionAmount = connectionRepository.count();

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        String jsonInString = mapper.writerWithView(Views.DetailCombination.class).writeValueAsString(comb);

        combinationService.saveCombination(jsonInString, comb.getId());
        assertThat(connectionRepository.count()).isEqualTo(connectionAmount);

    }

    @Test
    @Transactional
    public void amountOfProductInCombsSameAfterSaving() throws Exception{
        Product product = new Product();
        productRepository.save(product);

        User user = new User ("test","test", "test");
        user.setPassword("j");
        userRepository.save(user);

        Combination comb = user.addOwnCombination();
        comb.setName("name");
        ProductInComb productInComb = comb.addProductInComb(product);

        combinationRepository.save(comb);

        long productInCombAmount = productInCombRepository.count();

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        String jsonInString = mapper.writerWithView(Views.DetailCombination.class).writeValueAsString(comb);

        combinationService.saveCombination(jsonInString, comb.getId());

        assertThat(productInCombRepository.count()).isEqualTo(productInCombAmount);

    }
}