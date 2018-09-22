package de.sopro.logic;

import de.sopro.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

class CompatibilityLogicTest {

    private static CompatibilityLogic logic;
    private Connection connection1;
    private Connection connection2;
    private Connection connection3;
    private Connection connection4;

    private Product product1;
    private Product product2;
    private Product product3;
    private Product product4;
    private Product product5;
    private Product product6;
    private Product product7;

    private Product product8;
    private Product product9;
    private Product product10;
    private Product product11;

    private Format format1;
    private Format format2;
    private Format format3;
    private Format format4;
    private Format format5;
    private Format format6;
    private Format format7;
    private Format format8;
    private Format format9;


    @BeforeEach
    void init() throws Exception {

        //setting a logic instance up
        logic = CompatibilityLogic.getInstance();

        //user for combination
        User user = new User("y", "x", "x");
        Combination comb = user.addOwnCombination();

        //products
        product1 = spy(Product.class);
        product2 = spy(Product.class);
        product3 = spy(Product.class);
        product4 = spy(Product.class);
        product5 = spy(Product.class);
        product6 = spy(Product.class);
        product7 = spy(Product.class);
        product8 = spy(Product.class);
        product9 = spy(Product.class);
        product10 = spy(Product.class);
        product11 = spy(Product.class);

        product1.setName("p1");
        product2.setName("p2");
        product3.setName("p3");
        product4.setName("p4");
        product5.setName("p5");
        product6.setName("p6");
        product7.setName("p7");
        product8.setName("p8");
        product9.setName("p9");
        product10.setName("p10");
        product11.setName("p11");


        //formats
        format1 = new Format();
        format2 = new Format();
        format3 = new Format();
        format4 = new Format();
        format5 = new Format();

        format6 = new Format();
        format7 = new Format();
        format8 = new Format();
        format9 = new Format();

        //format has version 1-3
        FormatVersion version1 = format1.addVersion("v1");
        FormatVersion version2 = format1.addVersion("v2");
        FormatVersion version3 = format1.addVersion("v3");

        FormatVersion version4 = format2.addVersion("v1");
        FormatVersion version5 = format2.addVersion("v2");
        FormatVersion version6 = format2.addVersion("v3");

        FormatVersion version7 = format3.addVersion("v1");
        FormatVersion version8 = format3.addVersion("v2");
        FormatVersion version9 = format3.addVersion("v3");

        FormatVersion version10 = format4.addVersion("v1");
        FormatVersion version11 = format4.addVersion("v2");
        FormatVersion version12 = format4.addVersion("v3");

        FormatVersion version13 = format5.addVersion("v1");
        FormatVersion version14 = format5.addVersion("v2");
        FormatVersion version15 = format5.addVersion("v3");


        FormatVersion version20 = format6.addVersion("20");
        FormatVersion version21 = format7.addVersion("21");
        FormatVersion version22 = format8.addVersion("22");
        FormatVersion version23 = format9.addVersion("23");


        format1.setName("format1");
        format2.setName("format2");
        format3.setName("format3");
        format4.setName("format4");
        format5.setName("format5");
        format6.setName("format6");
        format7.setName("format7");
        format8.setName("format8");
        format9.setName("format9");


        //--------------------------Product-Versions-----------------------------------------------
        // For testing we have a special setup in the formats. For more details look below.
        product1.addFormatIn(version1);
        product2.addFormatIn(version4);
        product3.addFormatIn(version7);
        product4.addFormatIn(version10);
        product5.addFormatIn(version13);
        product6.addFormatIn(version1);
        product7.addFormatIn(version1);

        product9.addFormatIn(version20);
        product10.addFormatIn(version21);
        product11.addFormatIn(version22);

        //product 8-> 9 -> 10 -> 11

        product8.addFormatOut(version20);
        product9.addFormatOut(version21);
        product10.addFormatOut(version22);


        product1.addFormatOut(version4);
        product2.addFormatOut(version7);
        product3.addFormatOut(version10);
        product4.addFormatOut(version10);
        product5.addFormatOut(version13);
        product6.addFormatOut(version1);
        product7.addFormatOut(version4);

        //Outproduct -> InVersion

        //product 1 -> product 2 over version 4
        //product 1 -> product 2 over version 4 ->product 3 over version 7
        //product 1 -> product 2 over version 4 ->product 3 over version 7 ->product 4 over version 10
        //
        //product 1 -/> product 5
        //product 6->1->2
        //product 6->7->2


        //--------------------------ProductInComb--------------------------------------------------------
        ProductInComb inComb1 = comb.addProductInComb(product1);
        ProductInComb inComb2 = comb.addProductInComb(product2);
        ProductInComb inComb3 = comb.addProductInComb(product3);
        ProductInComb inComb4 = comb.addProductInComb(product4);
        ProductInComb inComb5 = comb.addProductInComb(product5);

        //compatible prod 1 -> prod 2
        connection1 = comb.addConnection(inComb1, inComb2);
        //compatible prod 1 -> 3 with alternative over 2
        connection2 = comb.addConnection(inComb1, inComb3);
        //compatible prod 1 -> 4 with alternative over 2 and 3
        connection3 = comb.addConnection(inComb1, inComb4);
        //not compatible 1 -/> 5 !!!!!
        connection4 = comb.addConnection(inComb1, inComb5);
    }

    @Test
    void logicInstanceNotNull() {
        assertThat(logic).isNotNull();
    }

    //----------------------------------------Compatibility Checks------------------------------------------------------

    @Test
    void isNotCompatibleWhenNot() {
        assertThat(logic.getCompatibility(product1, product5)).isEqualTo(Compatibility.NOT_COMPATIBLE);
    }

    @Test
    void isNotCompatibleWhenMultiplelevl1() {
        assertThat(logic.getCompatibility(product6, product2)).isEqualTo(Compatibility.COMPATIBLE_WITH_ALTERNATIVE);
    }

    @Test
    void isYesCompatibleWhenYes() {
        assertThat(logic.getCompatibility(product1, product2)).isEqualTo(Compatibility.COMPATIBLE);
    }

    @Test
    void isCompatibleWhenThereAre1Alternative() {
        assertThat(logic.getCompatibility(product1, product3)).isEqualTo(Compatibility.COMPATIBLE_WITH_ALTERNATIVE);
    }

    @Test
    void isCompatibleWhenThereAreAlternativeProductsLVL2() {
        assertThat(logic.getCompatibility(product8, product11)).isEqualTo(Compatibility.COMPATIBLE_WITH_ALTERNATIVE);
    }


    @Test
    void isCompatibleWhenThereAre2Alternative() {
        assertThat(logic.getCompatibility(product1, product4)).isEqualTo(Compatibility.COMPATIBLE_WITH_ALTERNATIVE);
    }


    @Test
    void isNotCompatibleWhenAlternativeIsNot() {
        assertThat(logic.getCompatibility(product1, product5)).isEqualTo(Compatibility.NOT_COMPATIBLE);
    }


    @Test
    void compatibilityCheckerTestWhenYes() {
        assertThat(logic.compatibilityCheck(
                connection1.getSourceProduct().getProduct(),
                connection1.getTargetProduct().getProduct())
        ).isEqualTo(Compatibility.COMPATIBLE);
    }

    @Test
    void compatibilityCheckerTestWhenNo() {
        assertThat(logic.compatibilityCheck(
                product1,
                product5)
        ).isEqualTo(Compatibility.NOT_COMPATIBLE);
    }


    //----------------------------------------------------------Get-Alternatives----------------------------------------
    @Test
    void checkGetAlternativesWhen1() {
        List<AlternativeProducts> prods = new ArrayList<>();
        AlternativeProducts alternatives = new AlternativeProducts();
        alternatives.addProductToAlternative(product2);
        prods.add(alternatives);

        assertThat(logic.getAlternativeProducts(product1, product3).toString()).isEqualTo(prods.toString());
    }

    @Test
    void checkGetAlternativesWhen2() {
        List<AlternativeProducts> prods = new ArrayList<>();
        AlternativeProducts alternatives = new AlternativeProducts();
        alternatives.addProductToAlternative(product2);
        alternatives.addProductToAlternative(product3);
        prods.add(alternatives);

        assertThat(logic.getAlternativeProducts(product1, product4).toString()).isEqualTo(prods.toString());
    }

    @Test
    void checkGetAlternativesWhenNo() {
        List<AlternativeProducts> alternativeProducts = new ArrayList<>();

        assertThat(logic.getAlternativeProducts(product1, product5)).isEqualTo(alternativeProducts);
    }


    @Test
    void checkGetAlternativesWhenMultipleWith1() {
        List<AlternativeProducts> prods = new ArrayList<>();
        AlternativeProducts alternatives = new AlternativeProducts();
        AlternativeProducts alternatives2 = new AlternativeProducts();

        alternatives.addProductToAlternative(product1);
        alternatives2.addProductToAlternative(product7);
        prods.add(alternatives);
        prods.add(alternatives2);


        assertThat(logic.getAlternativeProducts(product6, product2)).isEqualTo(prods);
    }


    //----------------------------------------------------------------------------------------------------------------------

}
