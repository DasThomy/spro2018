package de.sopro.services;

import com.fasterxml.jackson.databind.JsonMappingException;
import de.sopro.exceptions.InvalidDataException;
import de.sopro.model.*;
import de.sopro.repository.CombinationRepository;
import de.sopro.repository.FormatVersionRepository;
import de.sopro.repository.ProductRepository;
import de.sopro.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.naming.NameAlreadyBoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ProductServiceUploadTest {

    //Mocked Repositories
    private ProductRepository productRepositoryMock;
    private FormatVersionRepository formatVersionRepositoryMock;
    private TagRepository tagRepositoryMock;
    private CombinationRepository combinationRepositoryMock;

    //Mocked Services
    private TagService tagServiceMock;
    private FormatService formatServiceMock;
    private TransactionHelper transactionHelper;

    private ProductService productService;

    @BeforeEach
    public void setUp() {
        productRepositoryMock = mock(ProductRepository.class);
        formatVersionRepositoryMock = mock(FormatVersionRepository.class);
        tagRepositoryMock = mock(TagRepository.class);
        tagServiceMock = mock(TagService.class);
        formatServiceMock = mock(FormatService.class);
        combinationRepositoryMock = mock(CombinationRepository.class);
        transactionHelper = spy(TransactionHelper.class);
        productService = new ProductService(productRepositoryMock, tagRepositoryMock, formatVersionRepositoryMock, tagServiceMock, formatServiceMock, transactionHelper, combinationRepositoryMock);
    }

    @Test
    public void testUploadEmptyJSONThrowsJsonMappingException() {
        String jsonData = "";

        assertThatThrownBy(() -> productService.uploadProducts(jsonData)).isInstanceOf(JsonMappingException.class);
    }

    @Test
    public void testUploadEmptyProductsJsonThrowsInvalidDataException() {
        String jsonData = "{\"products\": []}";

        assertThatThrownBy(() -> productService.uploadProducts(jsonData)).isInstanceOf(InvalidDataException.class);
    }

    @Test
    public void testUploadMinimalProductJSONCreateProduct() throws IOException, InvalidDataException {
        String jsonData = "{\"products\":[{\"name\": \"TP Modeller\",\"organisation\": \"TP\",\"version\": \"1.0\",\"tags\": [],\"formatIn\":  [],\"formatOut\": []}]}";

        List<Product> createdProducts = productService.uploadProducts(jsonData);

        assertThat(createdProducts).hasSize(1);
    }

    @Test
    public void testUploadFullProductJSONCreateProduct() throws IOException, InvalidDataException {
        String jsonData = "{\"products\":[{\"name\":\"TPModeller\",\"organisation\":\"TP\",\"version\":\"1.0\",\"date\":1534943388,\"tags\":[\"3D\",\"Modeller\",\"Visualisierung\",\"Modellierung\"],\"logo\":\"TP_Modeller_10.png\",\"formatIn\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"BCF\",\"version\":\"1.0\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"DWG\",\"version\":\"5\",\"compatibilityDegree\":\"flexible\"}]}]}";
        when(tagServiceMock.createOrReturn(anyString())).thenReturn(new Tag());
        when(formatServiceMock.createOrReturnFormat(anyString(), any())).thenReturn(new Format());

        List<Product> createdProducts = productService.uploadProducts(jsonData);

        assertThat(createdProducts).hasSize(1);
    }

    @Test
    public void testUploadMinimalProductCreatesCorrectProduct() throws IOException, InvalidDataException {
        String jsonData = "{\"products\":[{\"name\": \"TP Modeller\",\"organisation\": \"TP\",\"version\": \"1.0\",\"tags\": [],\"formatIn\":  [],\"formatOut\": []}]}";

        when(productRepositoryMock.save(any(Product.class))).thenAnswer(new Answer<Product>() {
            @Override
            public Product answer(InvocationOnMock invocation) {
                return invocation.getArgument(0);
            }
        });

        Product createdProduct = productService.uploadProducts(jsonData).get(0);

        assertThat(createdProduct.getName()).isEqualTo("TP Modeller");
        assertThat(createdProduct.isCertified()).isFalse();
        assertThat(createdProduct.getOrganisation()).isEqualTo("TP");
        assertThat(createdProduct.getVersion()).isEqualTo("1.0");
        assertThat(createdProduct.getReleaseDate()).isEqualTo(0);
        assertThat(createdProduct.getLogoName()).isNull();
        assertThat(createdProduct.getTags()).isEmpty();
        assertThat(createdProduct.getFormatInList()).isEmpty();
        assertThat(createdProduct.getFormatOutList()).isEmpty();
    }

    @Test
    public void testUploadProductTwiceCreatesOne() throws IOException, InvalidDataException {
        String jsonData = "{\"products\":[{\"name\": \"TP Modeller\",\"organisation\": \"TP\",\"version\": \"1.0\",\"tags\": [],\"formatIn\":  [],\"formatOut\": []}]}";

        when(productRepositoryMock.save(any(Product.class))).thenAnswer(new Answer<Product>() {
            @Override
            public Product answer(InvocationOnMock invocation) {
                return invocation.getArgument(0);
            }
        });

        when(productRepositoryMock.existsByNameAndVersion("TP Modeller", "1.0"))
                .thenReturn(false) //first time no product exists
                .thenReturn(true); //second time a product is existing

        assertThat(productService.uploadProducts(jsonData)).hasSize(1);
        assertThat(productService.uploadProducts(jsonData)).hasSize(0);

        verify(productRepositoryMock, times(2)).existsByNameAndVersion("TP Modeller", "1.0");
        verify(productRepositoryMock, times(1)).save(any(Product.class));
    }

    @Test
    public void testUploadMaxmialProductCreatesCorrectProduct() throws IOException, InvalidDataException, NameAlreadyBoundException {
        String jsonData = "{\"products\":[{\"name\":\"TP Modeller\",\"organisation\":\"TP\",\"version\":\"1.0\",\"date\":1534943388,\"tags\":[\"3D\",\"Modeller\",\"Visualisierung\",\"Modellierung\"],\"logo\":\"TP_Modeller_10.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"BCF\",\"version\":\"1.0\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"DWG\",\"version\":\"5\",\"compatibilityDegree\":\"flexible\"}]}]}";

        //Create Tags, that are expected to be created
        List<Tag> expectedTags = new ArrayList<>();
        Tag t3D = new Tag();
        t3D.setName("3D");
        expectedTags.add(t3D);

        Tag tModellierung = new Tag();
        tModellierung.setName("Modellierung");
        expectedTags.add(tModellierung);

        Tag tVisualisierung = new Tag();
        tVisualisierung.setName("Visualisierung");
        expectedTags.add(tVisualisierung);

        Tag tModeller = new Tag();
        tModeller.setName("Modeller");
        expectedTags.add(tModeller);

        when(tagServiceMock.getTag("3D")).thenReturn(Optional.of(t3D));
        when(tagServiceMock.getTag("Modellierung")).thenReturn(Optional.of(tModellierung));
        when(tagServiceMock.getTag("Visualisierung")).thenReturn(Optional.of(tVisualisierung));
        when(tagServiceMock.getTag("Modeller")).thenReturn(Optional.of(tModeller));


        //Create expected in and out FormatVersions
        List<FormatVersion> expectedInFormats = new ArrayList<>();
        List<FormatVersion> expectedOutFormats = new ArrayList<>();

        FormatVersion fv2x0 = new FormatVersion();
        expectedInFormats.add(fv2x0);
        expectedOutFormats.add(fv2x0);

        FormatVersion fv10 = new FormatVersion();
        expectedInFormats.add(fv10);

        FormatVersion fv5 = new FormatVersion();
        expectedOutFormats.add(fv5);

        when(formatServiceMock.getFormatVersion("IFC", "2x0")).thenReturn(Optional.of(fv2x0));
        when(formatServiceMock.getFormatVersion("BCF", "1.0")).thenReturn(Optional.of(fv10));
        when(formatServiceMock.getFormatVersion("DWG", "5")).thenReturn(Optional.of(fv5));


        when(productRepositoryMock.save(any(Product.class))).thenAnswer(new Answer<Product>() {
            @Override
            public Product answer(InvocationOnMock invocation) {
                return invocation.getArgument(0);
            }
        });

        //Execute action
        Product createdProduct = productService.uploadProducts(jsonData).get(0);


        //Check if created product is as expected
        assertThat(createdProduct.getName()).isEqualTo("TP Modeller");
        assertThat(createdProduct.isCertified()).isTrue();
        assertThat(createdProduct.getOrganisation()).isEqualTo("TP");
        assertThat(createdProduct.getVersion()).isEqualTo("1.0");
        assertThat(createdProduct.getReleaseDate()).isEqualTo(1534943388);
        assertThat(createdProduct.getLogoName()).isEqualTo("TP_Modeller_10.png");
        assertThat(createdProduct.getTags()).containsAll(expectedTags);
        assertThat(createdProduct.getFormatInList()).containsAll(expectedInFormats);
        assertThat(createdProduct.getFormatOutList()).containsAll(expectedOutFormats);
    }

    @Test
    public void testUploadMultipleProducts() throws IOException, InvalidDataException, NameAlreadyBoundException {
        String jsonData = "{\"products\":[{\"name\":\"TP Modeller\",\"organisation\":\"TP\",\"version\":\"1.0\",\"date\":1534943388,\"tags\":[\"3D\",\"Modeller\",\"Visualisierung\",\"Modellierung\"],\"logo\":\"TP_Modeller_10.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"BCF\",\"version\":\"1.0\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"DWG\",\"version\":\"5\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"3D-Modeller\",\"organisation\":\"IGD\",\"version\":\"3\",\"date\":1531573788,\"tags\":[\"3D\",\"Modeller\",\"IFC\"],\"logo\":\"IGD_Modeller.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"gbXML\",\"version\":\"2\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"DWG\",\"version\":\"5\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"TP Modeller\",\"organisation\":\"TP\",\"version\":\"2.0\",\"date\":1526994588,\"tags\":[\"3D\",\"Modeller\",\"Visualisierung\",\"Modellierung\"],\"logo\":\"TP_Modeller_20.png\",\"certified\":\"false\",\"formatIn\":[{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"},{\"type\":\"FBX\",\"version\":\"1.0\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"COBie\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"}],\"formatOut\":[{\"type\":\"DWG\",\"version\":\"6\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"COBie\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"BCF\",\"version\":\"2.0\",\"compatibilityDegree\":\"strict\"}]},{\"name\":\"TP Modeller 4D\",\"organisation\":\"TP\",\"version\":\"1.0\",\"date\":1534511388,\"tags\":[\"4D\",\"Modeller\",\"Visualisierung\"],\"logo\":\"TP_Modeller_4D.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"RVT\",\"version\":\"1.05\",\"compatibilityDegree\":\"strict\"},{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"},{\"type\":\"BCF\",\"version\":\"2.0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"MPP\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"}],\"formatOut\":[{\"type\":\"DWG\",\"version\":\"6\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"Atom\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"},{\"type\":\"MPP\",\"version\":\"10\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"Ultimate Modeller\",\"organisation\":\"IGD\",\"version\":\"2.0\",\"date\":1535202588,\"tags\":[\"4D\",\"Modeller\",\"Visualisierung\",\"Modellierung\"],\"logo\":\"IGD_Modeller_Ultimate.png\",\"certified\":\"false\",\"formatIn\":[{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"},{\"type\":\"MPP\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"Atom\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"gbXML\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"DWG\",\"version\":\"6\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"RVT\",\"version\":\"1.05\",\"compatibilityDegree\":\"strict\"},{\"type\":\"MPP\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"Building Scheduler\",\"organisation\":\"e-task\",\"version\":\"1.0\",\"date\":1480424988,\"tags\":[\"Time\",\"Scheduler\",\"Planning\",\"Project Management\"],\"logo\":\"scheduler.png\",\"certified\":\"true\",\"formatIn\":[],\"formatOut\":[{\"type\":\"Atom\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"Schedule Manager\",\"organisation\":\"TP\",\"version\":\"2.5\",\"date\":1494335388,\"tags\":[\"Scheduler\",\"Manager\",\"Timetable\"],\"logo\":\"TP_Scheduler.png\",\"certified\":\"false\",\"formatIn\":[],\"formatOut\":[{\"type\":\"Atom\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"MPP\",\"version\":\"10\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"BIM Renderer\",\"organisation\":\"IGD\",\"version\":\"2.0\",\"date\":1517490588,\"tags\":[\"Renderer\",\"Visualisierung\",\"BIM\"],\"logo\":\"IGD_Renderer.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"FBX\",\"version\":\"1.0\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"FBX\",\"version\":\"2.0\",\"compatibilityDegree\":\"flexible\"}],\"formatOut\":[{\"type\":\"AVI\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"FBX\",\"version\":\"2.0\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"FBX-IFC Converter\",\"organisation\":\"BIM Converter\",\"version\":\"1.0\",\"date\":1490965788,\"tags\":[\"Converter\",\"IFC\",\"FBX\"],\"logo\":\"Converter_FBX_ICF.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"FBX\",\"version\":\"2.0\",\"compatibilityDegree\":\"flexible\"}],\"formatOut\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"}]},{\"name\":\"FBX-IFC Converter\",\"organisation\":\"BIM Converter\",\"version\":\"2.0\",\"date\":1490965788,\"tags\":[\"Converter\",\"IFC\",\"FBX\"],\"logo\":\"Converter_FBX_ICF.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"FBX\",\"version\":\"2.0\",\"compatibilityDegree\":\"flexible\"}],\"formatOut\":[{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"}]},{\"name\":\"Reinforcement Tool\",\"organisation\":\"RIB\",\"version\":\"1.0\",\"date\":1491052188,\"tags\":[\"Reinforcement\"],\"logo\":\"reinforcement.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"DWG\",\"version\":\"4\",\"compatibilityDegree\":\"flexible\"}],\"formatOut\":[{\"type\":\"RFXML\",\"version\":\"3.0\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"Atom\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"Reinforcement Tool\",\"organisation\":\"RIB\",\"version\":\"2.0\",\"date\":1491052188,\"tags\":[\"Reinforcement\"],\"logo\":\"reinforcement_2.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"DWG\",\"version\":\"5\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"RFXML\",\"version\":\"3.0\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"BCF\",\"version\":\"2.0\",\"compatibilityDegree\":\"strict\"}]},{\"name\":\"Facility Manager\",\"organisation\":\"e-task\",\"version\":\"2.0\",\"date\":1481807388,\"tags\":[\"Facility Manager\",\"BIM\",\"Facililty\"],\"logo\":\"Facility_Management_2.png\",\"certified\":\"false\",\"formatIn\":[{\"type\":\"COBie\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"}],\"formatOut\":[]},{\"name\":\"Facility Manager\",\"organisation\":\"e-task\",\"version\":\"4.0\",\"date\":1513343388,\"tags\":[\"Facility Manager\",\"BIM\",\"Facililty\"],\"logo\":\"Facility_Management_4.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"COBie\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[]},{\"name\":\"IFC COBie Converter\",\"organisation\":\"BIM-Convert\",\"version\":\"1.0\",\"date\":1516021788,\"tags\":[\"Converter\",\"IFC\",\"COBie\"],\"logo\":\"Converter_2.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"COBie\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"Angebotersteller\",\"organisation\":\"RIB\",\"version\":\"1.0\",\"date\":1527599388,\"tags\":[\"Angebot\",\"BIM\",\"Bauen\"],\"logo\":\"Angebotersteller.png\",\"certified\":\"false\",\"formatIn\":[{\"type\":\"GAEB\",\"version\":\"X83\",\"compatibilityDegree\":\"strict\"},{\"type\":\"GAEB\",\"version\":\"X31\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"GAEB\",\"version\":\"X83\",\"compatibilityDegree\":\"strict\"}]},{\"name\":\"BIM Angebote\",\"organisation\":\"TP\",\"version\":\"2.0\",\"date\":1524575388,\"tags\":[\"Angebot\",\"Angebote\"],\"logo\":\"TP_Angebote.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"GAEB\",\"version\":\"X83\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"GAEB\",\"version\":\"X84\",\"compatibilityDegree\":\"strict\"}]},{\"name\":\"TP Mengenermittler\",\"organisation\":\"TP\",\"version\":\"1.0\",\"date\":1510060188,\"tags\":[\"Mengenermittlung\",\"Mengen\",\"Planen\"],\"logo\":\"TP_Mengenermittler.png\",\"certified\":\"false\",\"formatIn\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"GAEB\",\"version\":\"X31\",\"compatibilityDegree\":\"strict\"}]},{\"name\":\"CDE im Bau\",\"organisation\":\"TP\",\"version\":\"2.0\",\"date\":0,\"tags\":[\"CDE\",\"BIM\",\"Controlling\"],\"logo\":\"TP_CDE_2.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"},{\"type\":\"DWG\",\"version\":\"6\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"PDF\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"}],\"formatOut\":[{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"},{\"type\":\"DWG\",\"version\":\"6\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"PDF\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"CDE im Bau\",\"organisation\":\"TP\",\"version\":\"1.0\",\"date\":1507640988,\"tags\":[\"CDE\",\"BIM\",\"Controlling\"],\"logo\":\"TP_CDE_1.png\",\"certified\":\"false\",\"formatIn\":[{\"type\":\"DWG\",\"version\":\"4\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"DWG\",\"version\":\"5\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"DWG\",\"version\":\"6\",\"compatibilityDegree\":\"flexible\"}],\"formatOut\":[{\"type\":\"DWG\",\"version\":\"4\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"DWG\",\"version\":\"5\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"DWG\",\"version\":\"6\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"PDF\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"BFC Maker\",\"organisation\":\"BIM Converter\",\"version\":\"1.0\",\"date\":1477055388,\"tags\":[\"Converter\",\"BCF\",\"DWG\",\"RVT\"],\"logo\":\"Converter_3.png\",\"certified\":\"false\",\"formatIn\":[{\"type\":\"DWG\",\"version\":\"6\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"RVT\",\"version\":\"1.05\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"BCF\",\"version\":\"1.0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"BCF\",\"version\":\"2.0\",\"compatibilityDegree\":\"strict\"}]},{\"name\":\"Wärmesimulation\",\"organisation\":\"e-task\",\"version\":\"1.0\",\"date\":1470488988,\"tags\":[\"Simulation\",\"Simulator\",\"Wärme\",\"Bauphysik\"],\"logo\":\"Waermesimulation.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"gbXML\",\"version\":\"2\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"}]},{\"name\":\"Simulation Tageslicht\",\"organisation\":\"IGD\",\"version\":\"2\",\"date\":1518700188,\"tags\":[\"Simulation\",\"Licht\",\"Tageslicht\",\"Planen\"],\"logo\":\"Tageslicht.png\",\"certified\":\"false\",\"formatIn\":[{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"MP4\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"BIM Simulator\",\"organisation\":\"IGD\",\"version\":\"1\",\"date\":1491052188,\"tags\":[\"Simulation\",\"Bauphysik\",\"Tageslicht\",\"Wärme\",\"Taupunkt\",\"Planen\"],\"logo\":\"BIM_Simulator.png\",\"certified\":\"false\",\"formatIn\":[{\"type\":\"gbXML\",\"version\":\"2\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"DWG\",\"version\":\"6\",\"compatibilityDegree\":\"flexible\"}]},{\"name\":\"BIM Converter\",\"organisation\":\"TP\",\"version\":\"1.0\",\"date\":1533733788,\"tags\":[\"Converter\",\"BIM\",\"IFC\",\"COBie\",\"gbXML\"],\"logo\":\"Converter_4.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"COBie\",\"version\":\"\",\"compatibilityDegree\":\"flexible\"},{\"type\":\"gbXML\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"},{\"type\":\"gbXML\",\"version\":\"2\",\"compatibilityDegree\":\"strict\"}]},{\"name\":\"IFC Converter\",\"organisation\":\"BIM Converter\",\"version\":\"1.0\",\"date\":1535116188,\"tags\":[\"Converter\",\"IFC\"],\"logo\":\"Converter_5.png\",\"certified\":\"true\",\"formatIn\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"}],\"formatOut\":[{\"type\":\"IFC\",\"version\":\"2x0\",\"compatibilityDegree\":\"strict\"},{\"type\":\"IFC\",\"version\":\"4\",\"compatibilityDegree\":\"strict\"}]}]}";

        when(tagServiceMock.createOrReturn(anyString())).thenAnswer(new Answer<Tag>() {
            @Override
            public Tag answer(InvocationOnMock invocation) {
                return new Tag();
            }
        });
        when(formatServiceMock.createOrReturnFormat(anyString(), any(CompatibilityDegree.class))).then(new Answer<Format>() {
            @Override
            public Format answer(InvocationOnMock invocation) {
                return new Format();
            }
        });

        //Execute action
        List<Product> createdProducts = productService.uploadProducts(jsonData);


        //Check if correct number of Products are created
        assertThat(createdProducts).hasSize(26);
    }
}