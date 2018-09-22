package de.sopro.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CombinationTest {

    @Test
    void addReadPermission() {
        User user = new User();
        Combination combination = new Combination();
        combination.addReadPermission(user);
        assertThat(combination.getReader()).contains(user);

        combination.addReadPermission(user);
        assertThat(combination.getReader()).containsOnlyOnce(user);

    }

    @Test
    void removeReadPermission() {
        User user = new User();
        Combination combination = new Combination();
        combination.addReadPermission(user);
        combination.removeReadPermission(user);

        assertThat(combination.getReader()).doesNotContain(user);
    }

    @Test
    void addConnection() {
        Combination combination = new Combination();
        Product p1 = new Product();
        Product p2 = new Product();

        ProductInComb product1 = combination.addProductInComb(p1);
        ProductInComb product2 = combination.addProductInComb(p2);

        Connection connection = combination.addConnection(product1, product2);
        assertThat(combination.getConnections()).contains(connection);

        Connection connection2 = combination.addConnection(product1, product2);

        assertThat(connection2).isNull();
    }

    @Test
    void removeConnection() {
        Combination combination = new Combination();
        Product p1 = new Product();
        Product p2 = new Product();

        ProductInComb product1 = combination.addProductInComb(p1);
        ProductInComb product2 = combination.addProductInComb(p2);

        Connection connection = combination.addConnection(product1, product2);
        combination.removeConnection(connection);
        assertThat(combination.getConnections()).doesNotContain(connection);
    }

    @Test
    void addProductInComb() {
        Combination combination = new Combination();
        Product product = new Product();
        ProductInComb productInComb = combination.addProductInComb(product);
        assertThat(combination.getProductsInComb()).contains(productInComb);

        combination.addProductInComb(product);
        assertThat(combination.getProductsInComb()).hasSize(2);
    }

    @Test
    void removeProductInComb() {
        Combination combination = new Combination();
        Product product = new Product();
        ProductInComb productInComb = combination.addProductInComb(product);

        combination.removeProductInComb(productInComb);
        assertThat(combination.getProductsInComb()).doesNotContain(productInComb);
    }
}