package de.sopro.services;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sopro.filter.Views;
import de.sopro.model.*;
import de.sopro.repository.*;
import de.sopro.security.SecurityService;
import javassist.tools.rmi.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.sasl.AuthenticationException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@Service
public class CombinationService {

    private final CombinationRepository combinationRepository;
    private final ProductRepository productRepository;
    private final SecurityService securityService;
    private final UserRepository userRepository;

    @Autowired
    public CombinationService(CombinationRepository combinationRepository,
                              ProductRepository productRepository,
                              SecurityService securityService,
                              ProductInCombRepository productInCombRepository,
                              ConnectionRepository connectionRepository,
                              UserRepository userRepository) {

        this.combinationRepository = combinationRepository;
        this.productRepository = productRepository;
        this.securityService = securityService;
        this.userRepository = userRepository;
    }

    public Optional<Combination> getCombination(int id) {
        return combinationRepository.findById(id);
    }

    public List<Combination> getOwnCombinations() {
        Optional<User> user = securityService.getLoggedInUser();

        if(!user.isPresent()) {
            return new ArrayList<>();
        }

        return user.get().getOwnCombinations();
    }

    public List<Combination> getSharedCombinations() {
        Optional<User> user = securityService.getLoggedInUser();

        if(!user.isPresent()) {
            return new ArrayList<>();
        }

        return user.get().getSharedCombinations();
    }

    public List<Combination> getPublicCombinations() {
        return combinationRepository.findAllPublic();
    }

    @Transactional
    public void saveCombination(String jsonCombination, int combID) throws IllegalArgumentException, IOException, ObjectNotFoundException {

        Optional<Combination> optionalComb = combinationRepository.findById(combID);

        if(!optionalComb.isPresent()){
            throw new IllegalArgumentException("Combination does not exist");
        }

        Combination comb = changeCombination(optionalComb.get(),jsonCombination);

        combinationRepository.save(comb);
    }

    @Transactional
    public Combination createCombination(String jsonCombination) throws IOException, ObjectNotFoundException{

        Optional<User> user = securityService.getLoggedInUser();
        if(!user.isPresent()){
            throw new AuthenticationException();
        }

        Combination comb = user.get().addOwnCombination();
        comb = changeCombination(comb, jsonCombination);
        combinationRepository.save(comb);
        return comb;
    }

    @Transactional
    protected Combination changeCombination(Combination comb, String jsonCombination) throws IOException, ObjectNotFoundException{
        comb.getConnections().clear();
        comb.getProductsInComb().clear();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonCombination);
        comb.setName(jsonNode.get("name").asText());
        comb.setPublicVisible(jsonNode.get("publicVisible").asBoolean());

        Map<Integer, ProductInComb> map = new HashMap<>();
        JsonNode productInCombsList = jsonNode.findValue("productsInComb");

        for(JsonNode productInCombsNode : productInCombsList){
            int jsonID = productInCombsNode.findValue("id").asInt();
            int xPosition = productInCombsNode.findValue("xPosition").asInt();
            int yPosition = productInCombsNode.findValue("yPosition").asInt();
            JsonNode productNode = productInCombsNode.findValue("product");
            Optional<Product> optProduct = productRepository.findById(productNode.findValue("id").asInt());

            if(!optProduct.isPresent()){
                throw new ObjectNotFoundException("Product doesn't exist");
            }
            ProductInComb productInComb = comb.addProductInComb(optProduct.get());
            productInComb.setyPosition(yPosition);
            productInComb.setxPosition(xPosition);
            map.put(jsonID, productInComb);
        }

        JsonNode connectionsList = jsonNode.findValue("connections");
        for(JsonNode connection : connectionsList){
            if(connection != null) {

                JsonNode jsonSourceID = connection.findValue("sourceProduct");
                JsonNode jsonTargetID = connection.findValue("targetProduct");
                if(jsonSourceID != null && jsonTargetID != null) {
                    int sourceID = jsonSourceID.asInt();
                    int targetID = jsonTargetID.asInt();

                    ProductInComb sourceProduct = map.get(sourceID);
                    ProductInComb targetProduct = map.get(targetID);
                    Connection newConnection = comb.addConnection(sourceProduct, targetProduct);

                    Compatibility compatibility = mapper.readValue(connection.findValue("compatibility").toString(), Compatibility.class);
                    newConnection.setCompatibility(compatibility);
                }
            }
        }


        JsonNode sharedUsers = jsonNode.findValue("sharedUsers");
        if (sharedUsers != null) {
            for(JsonNode jsonEmail : sharedUsers){

                String email = jsonEmail.asText();
                Optional<User> user = userRepository.findByEmail(email);
                user.ifPresent(comb::addReadPermission);
            }
        }
        return comb;
    }

    @Transactional
    public List<Combination> searchCombination(String searchRequest){
        return combinationRepository.findByNameContainingIgnoreCase(searchRequest);
    }

    @Transactional
    public void deleteCombination(int combID) throws AuthenticationException, NoSuchElementException {
        Optional<Combination> comb = combinationRepository.findById(combID);
        Optional<User> optionalUser = securityService.getLoggedInUser();

        if(!optionalUser.isPresent()){
            throw new AuthenticationException("Not logged in");
        }
        User user = optionalUser.get();

        if(comb.isPresent()){
            if(user.getId() != comb.get().getCreator().getId()){
                throw new AuthenticationException("Not the owner of the combination");
            }
            combinationRepository.delete(comb.get());
        }
        else{
            throw new NoSuchElementException("Combination not found");
        }
    }

    @Transactional
    public boolean isVisibleFor(User user, Combination combination){
        if(user.equals(combination.getCreator())){
            return true;
        }
        if(combination.isPublicVisible()){
            return true;
        }
        if(combination.getReader().contains(user)){
            return true;
        }
        return (user.getOrganisation() != null && user.getOrganisation().getSharedCombinations().contains(combination));
    }


    public SearchResult searchFor(String searchRequest){
        return new SearchResult(searchRequest);
    }

    public class SearchResult{
        public SearchResult(String searchRequest){
            List<Combination> combinations = searchCombination(searchRequest);

            publicCombinationList = getPublicCombinations();
            publicCombinationList.retainAll(combinations);

            sharedCombinationsList = getSharedCombinations();
            sharedCombinationsList.retainAll(combinations);

            ownCombinationsList = getOwnCombinations();
            ownCombinationsList.retainAll(combinations);
        }

        @JsonView(Views.BasicCombination.class)
        private List<Combination> publicCombinationList;

        @JsonView(Views.BasicCombination.class)
        private List<Combination> sharedCombinationsList;

        @JsonView(Views.BasicCombination.class)
        private List<Combination> ownCombinationsList;

        public List<Combination> getPublicCombinationList() {
            return publicCombinationList;
        }

        public List<Combination> getSharedCombinationsList() {
            return sharedCombinationsList;
        }

        public List<Combination> getOwnCombinationsList() {
            return ownCombinationsList;
        }
    }


}
