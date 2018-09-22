package de.sopro.services;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sopro.DTO.ProductDTO;
import de.sopro.DTO.ProductsJsonDTO;
import de.sopro.exceptions.InvalidDataException;
import de.sopro.model.*;
import de.sopro.repository.CombinationRepository;
import de.sopro.repository.FormatVersionRepository;
import de.sopro.repository.ProductRepository;
import de.sopro.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //Used Repositories
    private final ProductRepository productRepository;
    private final TagRepository tagRepository;
    private final FormatVersionRepository formatVersionRepository;
    private final CombinationRepository combinationRepository;

    //UsedServices
    private final TagService tagService;
    private final FormatService formatService;
    private final TransactionHelper transactionHelper;

    @Autowired
    public ProductService(ProductRepository productRepository, TagRepository tagRepository,
                          FormatVersionRepository formatVersionRepository, TagService tagService,
                          FormatService formatService, TransactionHelper transactionHelper,
                          CombinationRepository combinationRepository) {
        this.productRepository = productRepository;
        this.tagRepository = tagRepository;
        this.formatVersionRepository = formatVersionRepository;
        this.tagService = tagService;
        this.formatService = formatService;
        this.transactionHelper = transactionHelper;
        this.combinationRepository = combinationRepository;
    }

    public Optional<Product> getProduct(int id) {
        return productRepository.getProductById(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getAllCertified() {
        return productRepository.findByCertified(true);
    }

    public List<Product> searchProduct(String searchRequest, boolean onlyCertified) {
        String[] requests = searchRequest.split(" ");

        if (requests.length == 0) {
            return new ArrayList<>();
        }

        List<List<Product>> results = new ArrayList<>();
        for (String request : requests) {
            results.add(searchBySingleRequest(request));
        }

        List<Product> firstResult = results.get(0);

        for (List<Product> products : results) {
            firstResult.retainAll(products);
        }
        if (onlyCertified) {
            firstResult.retainAll(getAllCertified());
        }
        return firstResult;
    }

    public List<Product> getProductList(String search, boolean onlyCertified) {
        List<Product> products;
        if (search == null) {
            if (onlyCertified) {
                products = getAllCertified();
            } else {
                products = getAllProducts();
            }
        } else {
            products = searchProduct(search, onlyCertified);
        }
        return products;
    }


    private List<Product> searchBySingleRequest(String searchRequest) {

        List<Tag> tags = tagRepository.findByNameContainingIgnoreCase(searchRequest);

        List<Product> products = productRepository.findByNameContainingIgnoreCase(searchRequest);
        for (Tag tag : tags) {

            List<Product> newProducts = new ArrayList<>(tag.getProductsWithTag());

            newProducts.removeAll(products);
            products.addAll(newProducts);

        }

        return products;
    }

    @Transactional
    public void deleteProduct(int productID) throws IllegalStateException, NoSuchElementException {

        Optional<Product> optionalProduct = productRepository.findById(productID);

        if (!optionalProduct.isPresent()) {
            throw new NoSuchElementException("Product not found.");
        }
        Product product = optionalProduct.get();

        for (Combination combination : combinationRepository.findAll()) {
            for (ProductInComb productInComb : combination.getProductsInComb()) {
                if (productInComb.getProduct().equals(product)) {
                    throw new IllegalStateException("Product is in use, so it can't be deleted");
                }
            }
        }
        productRepository.delete(product);
    }

    public Product createProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setVersion(productDTO.getVersion());
        product.setReleaseDate(productDTO.getReleaseDate());
        product.setOrganisation(productDTO.getOrganisation());
        product.setCertified(productDTO.getCertified());
        for (String formatInString : productDTO.getFormatInList()) {

            int formatIn = Integer.parseInt(formatInString.replaceAll("[^0-9]", ""));

            FormatVersion formatVersion = formatVersionRepository.findById(formatIn).get();

            product.addFormatIn(formatVersion);
        }

        for (String formatOutString : productDTO.getFormatOutList()) {
            int formatOut = Integer.parseInt(formatOutString.replaceAll("[^0-9]", ""));
            FormatVersion formatVersion = formatVersionRepository.findById(formatOut).get();
            product.addFormatOut(formatVersion);
        }
        String tags = productDTO.getTags().replace(" ", "");
        String[] tagList = tags.split(",");

        for (String tagName : tagList) {
            Tag tag = tagService.createOrReturn(tagName);
            product.addTag(tag);
        }

        productRepository.save(product);
        return product;
    }

    /**
     * @param productsJSON
     * @return
     * @throws JsonMappingException
     * @throws InvalidDataException
     */
    public List<Product> uploadProducts(String productsJSON) throws IOException, JsonParseException, InvalidDataException {
        ProductsJsonDTO productsDTO = parseToProductsJsonDTO(productsJSON);

        validateProductsJsonDTO(productsDTO);

        //Create formatversions and tags
        for (ProductsJsonDTO.ProductDTO productDTO : productsDTO.getProducts()) {
            productDTO.getTags().stream().forEach(t -> tagService.createOrReturn(t));
            productDTO.getFormatIn().stream().forEach(f -> formatService.createOrReturnFormatVersion(f.getType(), f.getCompatibilityDegree(), f.getVersion()));
            productDTO.getFormatOut().stream().forEach(f -> formatService.createOrReturnFormatVersion(f.getType(), f.getCompatibilityDegree(), f.getVersion()));
        }


        //Creating Products
        List<Product> products = new ArrayList<>();
        for (ProductsJsonDTO.ProductDTO productDTO : productsDTO.getProducts()) {
            if (!productRepository.existsByNameAndVersion(productDTO.getName(), productDTO.getVersion())) {
                products.add(createProduct(productDTO));
            }
        }

        return products;
    }

    /**
     * Parses the given string to an ProductsJsonDTO object that contains multiple products
     *
     * @param productsJSON The string to parse
     * @return A ProductsJsonDTO object that contains the values
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    private ProductsJsonDTO parseToProductsJsonDTO(String productsJSON) throws IOException, JsonParseException {
        //Decode JSON to Object
        ObjectMapper mapper = new ObjectMapper();

        //enable the compabilitDegree Enum to be lower case in JSON file
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

        try {
            return mapper.readValue(productsJSON, ProductsJsonDTO.class);
        } catch (JsonMappingException e) {
            logger.error("Mapping error while parsing JSON: ", e);
            throw e;
        } catch (IOException e) {
            logger.error(e.toString(), e.getStackTrace());
            throw e;
        }
    }

    /**
     * Validation of Object data.
     *
     * @param productsDTO The ProductsJsonDTO object that needs to be validated
     * @throws InvalidDataException If the object is noch valid
     */
    private void validateProductsJsonDTO(ProductsJsonDTO productsDTO) throws InvalidDataException {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<ProductsJsonDTO>> violations = validator.validate(productsDTO);

        if (!violations.isEmpty()) {
            String errors = violations.stream().map(v -> v.getPropertyPath() + " = " + v.getInvalidValue() + ": " + v.getMessage() + " ").collect(Collectors.joining());
            InvalidDataException e = new InvalidDataException(errors);
            logger.error("Got invalid data in JSON file:", e);
            throw e;
        }
    }


    public String addPicturetoOne(String picture, int id) {

        if (picture.equals("")) {
            return "errors were made by reading";
        }


        Optional<Product> product = productRepository.findById(id);

        if (product.isPresent()) {
            product.get().setPicture(picture);
            return "succefully settet picture";
        }

        return "no product found";


    }


    public void addLogoToProduct(MultipartFile file, int id) {
        Base64.Encoder base64 = java.util.Base64.getEncoder();

        transactionHelper.withTransaction(() -> {
            Optional<Product> optionalProduct = productRepository.getProductById(id);

            if (optionalProduct.isPresent()) {
                Product p = optionalProduct.get();
                try {
                    p.setLogoName(null);
                    p.setPicture(base64.encodeToString(file.getBytes()));
                    productRepository.save(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Add logo to a corresponding product, when the logo name is equal.
     *
     * @param files
     */
    public void addLogos(MultipartFile[] files) {
        Base64.Encoder base64 = java.util.Base64.getEncoder();

        for (MultipartFile file : files) {
            String logoName = file.getOriginalFilename();

            transactionHelper.withTransaction(() -> {
                List<Product> products = productRepository.getProductByLogoName(logoName);

                for (Product p : products) {
                    try {
                        p.setLogoName(null);
                        p.setPicture(base64.encodeToString(file.getBytes()));
                        productRepository.save(p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * Creates a single product from a ProductDTO
     *
     * @param productDTO
     * @return
     */
    private Product createProduct(ProductsJsonDTO.ProductDTO productDTO) {
        return transactionHelper.withTransaction(() -> {
            Product product = new Product();

            product.setName(productDTO.getName());
            product.setCertified(productDTO.isCertified());
            product.setOrganisation(productDTO.getOrganisation());
            product.setVersion(productDTO.getVersion());
            product.setReleaseDate(productDTO.getDate());
            product.setLogoName(productDTO.getLogo());

            //Add tags
            for (String tag : productDTO.getTags()) {
                Optional<Tag> optionalTag = tagService.getTag(tag);
                optionalTag.ifPresent(product::addTag);
            }

            //Add formatVersions
            for (ProductsJsonDTO.ProductDTO.FormatDTO formatDTO : productDTO.getFormatIn()) {
                Optional<FormatVersion> fv = formatService.getFormatVersion(formatDTO.getType(), formatDTO.getVersion());
                fv.ifPresent(product::addFormatIn);
            }

            for (ProductsJsonDTO.ProductDTO.FormatDTO formatDTO : productDTO.getFormatOut()) {
                Optional<FormatVersion> fv = formatService.getFormatVersion(formatDTO.getType(), formatDTO.getVersion());
                fv.ifPresent(product::addFormatOut);
            }

            return productRepository.save(product);
        });
    }
}
