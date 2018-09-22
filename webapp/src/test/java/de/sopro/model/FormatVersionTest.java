package de.sopro.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class FormatVersionTest {

    @Test
    void addFormatInProduct() {
        FormatVersion version= new FormatVersion();
        Product product = new Product();
        version.addFormatInProduct(product);
        assertThat(version.getFormatInProducts()).contains(product);
        assertThat(product.getFormatInList()).contains(version);
    }

    @Test
    void removeFormatInProduct() {
        FormatVersion version= new FormatVersion();
        Product product = new Product();
        version.addFormatInProduct(product);
        version.removeFormatInProduct(product);
        assertThat(version.getFormatInProducts()).doesNotContain(product);
        assertThat(product.getFormatInList()).doesNotContain(version);
    }

    @Test
    void addFormatOutProduct() {
        FormatVersion version= new FormatVersion();
        Product product = new Product();
        version.addFormatOutProduct(product);
        assertThat(version.getFormatOutProducts()).contains(product);
        assertThat(product.getFormatOutList()).contains(version);
    }

    @Test
    void removeFormatOutProduct() {
        FormatVersion version= new FormatVersion();
        Product product = new Product();
        version.addFormatOutProduct(product);
        version.removeFormatOutProduct(product);
        assertThat(version.getFormatOutProducts()).doesNotContain(product);
        assertThat(product.getFormatOutList()).doesNotContain(version);
    }

    @Test
    void addFormatInProductTwice(){
        FormatVersion version= new FormatVersion();
        Product product = new Product();
        version.addFormatInProduct(product);
        version.addFormatInProduct(product);
        assertThat(version.getFormatInProducts()).containsOnlyOnce(product);
        assertThat(product.getFormatInList()).containsOnlyOnce(version);
    }

    @Test
    void addFormatOutProductTwice(){
        FormatVersion version= new FormatVersion();
        Product product = new Product();
        version.addFormatOutProduct(product);
        version.addFormatOutProduct(product);
        assertThat(version.getFormatOutProducts()).containsOnlyOnce(product);
        assertThat(product.getFormatOutList()).containsOnlyOnce(version);
    }
}