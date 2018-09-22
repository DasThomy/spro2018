package de.sopro.logic;

import com.fasterxml.jackson.annotation.JsonView;
import de.sopro.filter.Views;
import de.sopro.model.Product;

import java.util.ArrayList;
import java.util.List;

public class AlternativeProducts {

    @JsonView(Views.BasicProduct.class)
    private List<Product> products;

    public AlternativeProducts() {
        products = new ArrayList<>();
    }

    public List<Product> getProducts() {
        return products;
    }

    public void addProductToAlternative(Product newProduct) {
        this.products.add(newProduct);
    }

    @Override
    public String toString() {
        return products.toString();
    }

    @Override
    public boolean equals(Object o) {
        AlternativeProducts object = (AlternativeProducts) o;
        return this.products.equals(object.getProducts());
    }

}

