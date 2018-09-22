package de.sopro.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import de.sopro.filter.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Tag {

    @Id
    @JsonView(Views.DetailProduct.class)
    private String name;

    @ManyToMany
    @JsonBackReference
    private List<Product> products = new ArrayList<>();

    //------------------------------------------------------

    public Tag(){}

    //------------------------------------------------------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public List<Product> getProductsWithTag() {
        return products;
    }
}
