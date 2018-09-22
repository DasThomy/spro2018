package de.sopro.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
public class ProductTest {

    @Test
    void addFormatIn() {
        Product product = new Product();
        FormatVersion format = new FormatVersion();
        product.addFormatIn(format);
        assertThat(format.getFormatInProducts()).contains(product);
        assertThat(product.getFormatInList()).contains(format);
        product.addFormatIn(format);
        assertThat(format.getFormatInProducts()).containsOnlyOnce(product);
        assertThat(product.getFormatInList()).containsOnlyOnce(format);
    }

    @Test
    void removeFormatIn() {
        Product product = new Product();
        FormatVersion format = new FormatVersion();
        product.addFormatIn(format);
        product.removeFormatIn(format);
        assertThat(format.getFormatInProducts()).doesNotContain(product);
        assertThat(product.getFormatInList()).doesNotContain(format);
    }

    @Test
    void addFormatOut() {
        Product product = new Product();
        FormatVersion format = new FormatVersion();
        product.addFormatOut(format);
        assertThat(format.getFormatOutProducts()).contains(product);
        assertThat(product.getFormatOutList()).contains(format);
        product.addFormatOut(format);
        assertThat(format.getFormatOutProducts()).containsOnlyOnce(product);
        assertThat(product.getFormatOutList()).containsOnlyOnce(format);
    }

    @Test
    void removeFormatOut() {
        Product product = new Product();
        FormatVersion format = new FormatVersion();
        product.addFormatOut(format);
        product.removeFormatOut(format);
        assertThat(format.getFormatOutProducts()).doesNotContain(product);
        assertThat(product.getFormatOutList()).doesNotContain(format);
    }

    @Test
    void addTag() {
        Product product = new Product();
        Tag tag = new Tag();
        product.addTag(tag);
        assertThat(tag.getProductsWithTag()).contains(product);
        assertThat(product.getTags()).contains(tag);
        product.addTag(tag);
        assertThat(tag.getProductsWithTag()).containsOnlyOnce(product);
        assertThat(product.getTags()).containsOnlyOnce(tag);
    }

    @Test
    void removeTag() {
        Product product = new Product();
        Tag tag = new Tag();
        product.addTag(tag);

        product.removeTag(tag);

        assertThat(tag.getProductsWithTag()).doesNotContain(product);
        assertThat(product.getTags()).doesNotContain(tag);
    }
}