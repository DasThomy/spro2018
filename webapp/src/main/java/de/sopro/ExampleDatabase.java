package de.sopro;

import de.sopro.exceptions.InvalidDataException;
import de.sopro.logic.CompatibilityLogic;
import de.sopro.model.*;
import de.sopro.repository.*;
import de.sopro.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

import static de.sopro.model.CompatibilityDegree.FLEXIBLE;
import static de.sopro.model.CompatibilityDegree.STRICT;

@Configuration
public class ExampleDatabase {

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    OrganisationRepository organisationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FormatRepository formatRepository;

    @Autowired
    CombinationRepository combinationRepository;

    @Autowired
    FormatVersionRepository formatVersionRepository;

    @Autowired
    ConnectionRepository connectionRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    ProductInCombRepository productInCombRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductService productService;

    private final Logger logger = LoggerFactory.getLogger(ExampleDatabase.class);

    private int productAmount = 20;
    private int userAmount = 20;
    private int formatAmount = 8;
    private int combinationAmount = 10;
    private int formatVersionAmount = 20;
    private int productInCombAmount = 40;
    private int connectionAmount = 40;
    private int formatInProductAmount = 50;
    private int readPermissionsAmount = 30;
    private int tagAmount = 10;
    private int tagInProductAmount = 40;
    private int organisationAmount = 4;
    private int readPermissionsToOrganisationAmount = 5;

    private Random random = new Random();

    @Transactional
    public void generate() {

        logger.info("DELETE OLD DATA FROM DATABASE");

        combinationRepository.deleteAll();
        userRepository.deleteAll();
        organisationRepository.deleteAll();
        tagRepository.deleteAll();
        productRepository.deleteAll();
        formatRepository.deleteAll();

        logger.info("CREATE DATABASE");

        createOrganisations();

        logger.info(organisationAmount + " organisations created.");

        createUsers();

        logger.info(userAmount + " users created.");

        String jsonData = "{\"products\":[{\"name\":\"TP Modeller\",\"organisation\":\"TP\",\"version\":\"1.0\",\"date\":1534943388,\"tags\":[\"3D\",\"Modeller\",\"Visualisierung\",\"Modellierung\"],\"logo\":\"TP_Modeller_10.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"BCF\",\"version\":\"1.0\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"DWG\",\"version\":\"5\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"3D-Modeller\",\"organisation\":\"IGD\",\"version\":\"3\",\"date\":1531573788,\"tags\":[\"3D\",\"Modeller\",\"IFC\"],\"logo\":\"IGD_Modeller.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"gbXML\",\"version\":\"2\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"DWG\",\"version\":\"5\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"TP Modeller\",\"organisation\":\"TP\",\"version\":\"2.0\",\"date\":1526994588,\"tags\":[\"3D\",\"Modeller\",\"Visualisierung\",\"Modellierung\"],\"logo\":\"TP_Modeller_20.png\",\"certified\":\"false\",\"formatIn\":[{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"},{\"type\":\"FBX\",\"version\":\"1.0\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"COBie\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"}],\"formatOut\":[{\"type\":\"DWG\",\"version\":\"6\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"COBie\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"BCF\",\"version\":\"2.0\",\"compatibilityDegree\":\"strict\"}]},{\"name\":\"TP Modeller 4D\",\"organisation\":\"TP\",\"version\":\"1.0\",\"date\":1534511388,\"tags\":[\"4D\",\"Modeller\",\"Visualisierung\"],\"logo\":\"TP_Modeller_4D.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"RVT\",\"version\":\"1.05\",\"compatibilityDegree\":\"strict\"},{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"},{\"type\":\"BCF\",\"version\":\"2.0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"MPP\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"}],\"formatOut\":[{\"type\":\"DWG\",\"version\":\"6\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"Atom\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"},{\"type\":\"MPP\",\"version\":\"10\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"Ultimate Modeller\",\"organisation\":\"IGD\",\"version\":\"2.0\",\"date\":1535202588,\"tags\":[\"4D\",\"Modeller\",\"Visualisierung\",\"Modellierung\"],\"logo\":\"IGD_Modeller_Ultimate.png\",\"certified\":\"false\",\"formatIn\":[{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"},{\"type\":\"MPP\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"Atom\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"gbXML\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"DWG\",\"version\":\"6\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"RVT\",\"version\":\"1.05\",\"compatibilityDegree\":\"strict\"},{\"type\":\"MPP\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"Building Scheduler\",\"organisation\":\"e-task\",\"version\":\"1.0\",\"date\":1480424988,\"tags\":[\"Time\",\"Scheduler\",\"Planning\",\"Project Management\"],\"logo\":\"scheduler.png\",\"certified\":\"true\",\"formatIn\":[],\"formatOut\":[{\"type\":\"Atom\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"Schedule Manager\",\"organisation\":\"TP\",\"version\":\"2.5\",\"date\":1494335388,\"tags\":[\"Scheduler\",\"Manager\",\"Timetable\"],\"logo\":\"TP_Scheduler.png\",\"certified\":\"false\",\"formatIn\":[],\"formatOut\":[{\"type\":\"Atom\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"MPP\",\"version\":\"10\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"BIM Renderer\",\"organisation\":\"IGD\",\"version\":\"2.0\",\"date\":1517490588,\"tags\":[\"Renderer\",\"Visualisierung\",\"BIM\"],\"logo\":\"IGD_Renderer.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"FBX\",\"version\":\"1.0\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"FBX\",\"version\":\"2.0\",\"compatibilityDegree\":\"flexible\"}],\"formatOut\":[{\"type\":\"AVI\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"FBX\",\"version\":\"2.0\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"FBX-IFC Converter\",\"organisation\":\"BIM Converter\",\"version\":\"1.0\",\"date\":1490965788,\"tags\":[\"Converter\",\"IFC\",\"FBX\"],\"logo\":\"Converter_FBX_ICF.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"FBX\",\"version\":\"2.0\",\"compatibilityDegree\":\"flexible\"}],\"formatOut\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"}]},{\"name\":\"FBX-IFC Converter\",\"organisation\":\"BIM Converter\",\"version\":\"2.0\",\"date\":1490965788,\"tags\":[\"Converter\",\"IFC\",\"FBX\"],\"logo\":\"Converter_FBX_ICF.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"FBX\",\"version\":\"2.0\",\"compatibilityDegree\":\"flexible\"}],\"formatOut\":[{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"}]},{\"name\":\"Reinforcement Tool\",\"organisation\":\"RIB\",\"version\":\"1.0\",\"date\":1491052188,\"tags\":[\"Reinforcement\"],\"logo\":\"reinforcement.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"DWG\",\"version\":\"4\",\"compatibilityDegree\":\"flexible\"}],\"formatOut\":[{\"type\":\"RFXML\",\"version\":\"3.0\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"Atom\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"Reinforcement Tool\",\"organisation\":\"RIB\",\"version\":\"2.0\",\"date\":1491052188,\"tags\":[\"Reinforcement\"],\"logo\":\"reinforcement_2.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"DWG\",\"version\":\"5\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"RFXML\",\"version\":\"3.0\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"BCF\",\"version\":\"2.0\",\"compatibilityDegree\":\"strict\"}]},{\"name\":\"Facility Manager\",\"organisation\":\"e-task\",\"version\":\"2.0\",\"date\":1481807388,\"tags\":[\"Facility Manager\",\"BIM\",\"Facililty\"],\"logo\":\"Facility_Management_2.png\",\"certified\":\"false\",\"formatIn\":[{\"type\":\"COBie\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"}],\"formatOut\":[]},{\"name\":\"Facility Manager\",\"organisation\":\"e-task\",\"version\":\"4.0\",\"date\":1513343388,\"tags\":[\"Facility Manager\",\"BIM\",\"Facililty\"],\"logo\":\"Facility_Management_4.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"COBie\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[]},{\"name\":\"IFC COBie Converter\",\"organisation\":\"BIM-Convert\",\"version\":\"1.0\",\"date\":1516021788,\"tags\":[\"Converter\",\"IFC\",\"COBie\"],\"logo\":\"Converter_2.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"COBie\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"Angebotersteller\",\"organisation\":\"RIB\",\"version\":\"1.0\",\"date\":1527599388,\"tags\":[\"Angebot\",\"BIM\",\"Bauen\"],\"logo\":\"Angebotersteller.png\",\"certified\":\"false\",\"formatIn\":[{\"type\":\"GAEB\",\"version\":\"X83\",\"compatibilityDegree\":\"strict\"},{\"type\":\"GAEB\",\"version\":\"X31\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"GAEB\",\"version\":\"X83\",\"compatibilityDegree\":\"strict\"}]},{\"name\":\"BIM Angebote\",\"organisation\":\"TP\",\"version\":\"2.0\",\"date\":1524575388,\"tags\":[\"Angebot\",\"Angebote\"],\"logo\":\"TP_Angebote.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"GAEB\",\"version\":\"X83\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"GAEB\",\"version\":\"X84\",\"compatibilityDegree\":\"strict\"}]},{\"name\":\"TP Mengenermittler\",\"organisation\":\"TP\",\"version\":\"1.0\",\"date\":1510060188,\"tags\":[\"Mengenermittlung\",\"Mengen\",\"Planen\"],\"logo\":\"TP_Mengenermittler.png\",\"certified\":\"false\",\"formatIn\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"GAEB\",\"version\":\"X31\",\"compatibilityDegree\":\"strict\"}]},{\"name\":\"CDE im Bau\",\"organisation\":\"TP\",\"version\":\"2.0\",\"date\":0,\"tags\":[\"CDE\",\"BIM\",\"Controlling\"],\"logo\":\"TP_CDE_2.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"},{\"type\":\"DWG\",\"version\":\"6\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"PDF\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"}],\"formatOut\":[{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"},{\"type\":\"DWG\",\"version\":\"6\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"PDF\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"CDE im Bau\",\"organisation\":\"TP\",\"version\":\"1.0\",\"date\":1507640988,\"tags\":[\"CDE\",\"BIM\",\"Controlling\"],\"logo\":\"TP_CDE_1.png\",\"certified\":\"false\",\"formatIn\":[{\"type\":\"DWG\",\"version\":\"4\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"DWG\",\"version\":\"5\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"DWG\",\"version\":\"6\",\"compatibilityDegree\":\"flexible\"}],\"formatOut\":[{\"type\":\"DWG\",\"version\":\"4\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"DWG\",\"version\":\"5\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"DWG\",\"version\":\"6\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"PDF\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"BFC Maker\",\"organisation\":\"BIM Converter\",\"version\":\"1.0\",\"date\":1477055388,\"tags\":[\"Converter\",\"BCF\",\"DWG\",\"RVT\"],\"logo\":\"Converter_3.png\",\"certified\":\"false\",\"formatIn\":[{\"type\":\"DWG\",\"version\":\"6\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"RVT\",\"version\":\"1.05\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"BCF\",\"version\":\"1.0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"BCF\",\"version\":\"2.0\",\"compatibilityDegree\":\"strict\"}]},{\"name\":\"Wärmesimulation\",\"organisation\":\"e-task\",\"version\":\"1.0\",\"date\":1470488988,\"tags\":[\"Simulation\",\"Simulator\",\"Wärme\",\"Bauphysik\"],\"logo\":\"Waermesimulation.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"gbXML\",\"version\":\"2\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"}]},{\"name\":\"Simulation Tageslicht\",\"organisation\":\"IGD\",\"version\":\"2\",\"date\":1518700188,\"tags\":[\"Simulation\",\"Licht\",\"Tageslicht\",\"Planen\"],\"logo\":\"Tageslicht.png\",\"certified\":\"false\",\"formatIn\":[{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"MP4\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"BIM Simulator\",\"organisation\":\"IGD\",\"version\":\"1\",\"date\":1491052188,\"tags\":[\"Simulation\",\"Bauphysik\",\"Tageslicht\",\"Wärme\",\"Taupunkt\",\"Planen\"],\"logo\":\"BIM_Simulator.png\",\"certified\":\"false\",\"formatIn\":[{\"type\":\"gbXML\",\"version\":\"2\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"DWG\",\"version\":\"6\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"BIM Converter\",\"organisation\":\"TP\",\"version\":\"1.0\",\"date\":1533733788,\"tags\":[\"Converter\",\"BIM\",\"IFC\",\"COBie\",\"gbXML\"],\"logo\":\"Converter_4.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"COBie\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"gbXML\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"},{\"type\":\"gbXML\",\"version\":\"2\",\"compatibilityDegree\":\"strict\"}]},{\"name\":\"IFC Converter\",\"organisation\":\"BIM Converter\",\"version\":\"1.0\",\"date\":1535116188,\"tags\":[\"Converter\",\"IFC\"],\"logo\":\"Converter_5.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"}]}]}";
        try {
            productService.uploadProducts(jsonData);
        } catch (IOException e) {
            logger.error("Error automativally creating products: ", e);
        } catch (InvalidDataException e) {
            logger.error("Error automativally creating products: ", e);
        }


//        createFormats();
//
//        logger.info(formatAmount + " formats created.");
//
//        createFormatVersions();
//
//        logger.info(formatVersionAmount + " format versions created.");
//
//        createProducts();
//
//        logger.info(productAmount + " products created.");
//
//        addFormatToProduct();
//
//        logger.info(formatInProductAmount + " formats added to products.");

        createCombinations();

        logger.info(combinationAmount + " combinations created.");

        createProductInCombs();

        logger.info(productInCombAmount + " productInCombs created.");

        createConnections();

        logger.info(connectionAmount + " connections created.");

        addReadPermissionsToUser();

        logger.info(readPermissionsAmount + " read permissions distributed to users.");

        createTags();

        logger.info(tagAmount + " tags created.");

        addTagToProduct();

        logger.info(tagInProductAmount + " tags added distributed to products.");

        addReadPermissionsToOrganisation();

        logger.info(readPermissionsToOrganisationAmount + " read permissions distributed to organisations.");
    }

    //---------------- GENERATING ENTITIES -----------------------------------

    private void createCombinations() {
        for (int i = 0; i < combinationAmount; i++) {
            User user = getRandomUser();
            Combination combination = user.addOwnCombination();
            combination.setName("comb" + i);
            combination.setPublicVisible(random.nextBoolean());
            combinationRepository.save(combination);
        }
    }

    private void createProducts() {
        for (int i = 0; i < productAmount; i++) {
            Product product = new Product();
            product.setName("product" + i);
            product.setOrganisation("google");
            product.setReleaseDate(12345);
            product.setVersion("v1");
            product.setCertified(random.nextBoolean());
            productRepository.save(product);
        }
    }

    private void createTags(){
        for(int i = 0; i < tagAmount; i++){
            Tag tag = new Tag();
            tag.setName("Tag" + i);
            tagRepository.save(tag);
        }
    }

    private void createOrganisations(){
        for(int i = 0; i < organisationAmount; i++){
            Organisation organisation = new Organisation("orga" + i);
            organisationRepository.save(organisation);
        }
    }

    private void createUsers() {
        for (int i = 0; i < userAmount; i++) {
            User user = new User("max" + i + "@web.de", "max" + i, "mustermann" + i);
            Random randomGenerator = new Random();
            user.setPassword(encoder.encode("pass" + i));
            user.setFormOfAdress("Herr");
            user.setOrganisation(getRandomOrganisation());
            user.setTitle("Doktor");
            if(i == 5) {
                user.setRole(User.ROLE_ADMIN);
            }
            userRepository.save(user);
        }
    }

    private void createFormats() {
        for (int i = 0; i < formatAmount; i++) {
            Format format = new Format();
            format.setName("format" + i);
            if (random.nextBoolean()) {
                format.setCompatibilityDegree(STRICT);
            } else {
                format.setCompatibilityDegree(FLEXIBLE);
            }
            formatRepository.save(format);
        }
    }

    private void createFormatVersions() {
        for (int i = 0; i < formatVersionAmount; i++) {
            Format format = getRandomFormat();
            format.addVersion("v" + i);
            formatRepository.save(format);
        }
    }

    private void createProductInCombs() {
        for (int i = 0; i < productInCombAmount; i++) {
            Combination comb = getRandomCombination();
            ProductInComb  product = comb.addProductInComb(getRandomProduct());
            product.setxPosition(random.nextInt(500));
            product.setyPosition(random.nextInt(500));
            combinationRepository.save(comb);
        }
    }

    private void createConnections() {
        for (int i = 0; i < connectionAmount; i++) {
            Combination comb = getRandomCombination();
            List<ProductInComb> products = comb.getProductsInComb();
            if (products.size() >= 2) {
                int a = random.nextInt(products.size());
                int b = random.nextInt(products.size());
                while (b == a) {
                    b = random.nextInt(products.size());
                }
                Connection conn = comb.addConnection(products.get(a), products.get(b));

                if (conn != null) {
                    conn.setCompatibility(CompatibilityLogic.getInstance().getCompatibility
                            (products.get(a).getProduct(), products.get(b).getProduct()));
                }
                
            }
            combinationRepository.save(comb);
        }
    }

    //-------------ADDING ENTITY ASSOCIATIONS-----------------------------------

    private void addFormatToProduct() {
        for (int i = 0; i < formatInProductAmount; i++) {
            FormatVersion version = getRandomFormatVersion();
            Product product = getRandomProduct();
            if (random.nextBoolean()) {
                product.addFormatIn(version);
            } else {
                product.addFormatOut(version);
            }
            productRepository.save(product);
        }
    }

    private void addReadPermissionsToUser(){
        for(int i = 0; i < readPermissionsAmount; i++){
            getRandomCombination().addReadPermission(getRandomUser());
        }
    }

    private void addReadPermissionsToOrganisation(){
        for(int i = 0; i < readPermissionsToOrganisationAmount; i++){
            Combination combination = getRandomCombination();
            Organisation organisation = combination.getCreator().getOrganisation();
            organisation.addSharedCombination(combination);
        }
    }

    private void addTagToProduct(){
        for(int i = 0; i < tagInProductAmount; i++){
            getRandomProduct().addTag(getRandomTag());
        }
    }
    //----------------RANDOM ENTITY GENERATOR-----------------------------------

    private Organisation getRandomOrganisation(){
        List<Organisation> organisations = organisationRepository.findAll();
        int r = random.nextInt(organisations.size());
        return organisations.get(r);
    }

    private Tag getRandomTag(){
        List<Tag> tags = tagRepository.findAll();
        int r = random.nextInt(tags.size());
        return tags.get(r);
    }

    private Format getRandomFormat() {
        List<Format> formats = formatRepository.findAll();
        int r = random.nextInt(formats.size());
        return formats.get(r);
    }

    private Combination getRandomCombination() {
        List<Combination> combinations = combinationRepository.findAll();
        int r = random.nextInt(combinations.size());
        return combinations.get(r);
    }

    private Product getRandomProduct() {
        List<Product> products = productRepository.findAll();
        int r = random.nextInt(products.size());
        return products.get(r);
    }

    private User getRandomUser() {
        List<User> users = userRepository.findAll();
        int r = random.nextInt(users.size());
        return users.get(r);
    }

    private FormatVersion getRandomFormatVersion() {
        List<FormatVersion> versions = formatVersionRepository.findAll();
        int r = random.nextInt(versions.size());
        return versions.get(r);
    }

    //---------------- SETTER -----------------------------------

    public void setProductAmount(int productAmount) {
        this.productAmount = productAmount;
    }

    public void setUserAmount(int userAmount) {
        this.userAmount = userAmount;
    }

    public void setFormatAmount(int formatAmount) {
        this.formatAmount = formatAmount;
    }

    public void setCombinationAmount(int combinationAmount) {
        this.combinationAmount = combinationAmount;
    }

    public void setFormatVersionAmount(int formatVersionAmount) {
        this.formatVersionAmount = formatVersionAmount;
    }

    public void setProductInCombAmount(int productInCombAmount) {
        this.productInCombAmount = productInCombAmount;
    }

    public void setConnectionAmount(int connectionAmount) {
        this.connectionAmount = connectionAmount;
    }
}
