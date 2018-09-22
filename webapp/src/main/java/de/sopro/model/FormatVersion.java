package de.sopro.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import de.sopro.filter.Views;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class FormatVersion implements Comparable<FormatVersion> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @NonNull
    @JsonView(Views.DetailFormatVersion.class)
    private Format format;

    @NonNull
    @JsonView(Views.BasicFormatVersion.class)
    private String name;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Product> formatInProducts = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Product> formatOutProducts = new ArrayList<>();

    //------------------------------------------------------

    public FormatVersion(){}

    FormatVersion(Format format, String name){
        this.format = format;
        this.name = name;
    }

    //------------------------------------------------------

    public int getId() {
        return id;
    }
    public Format getFormat() {
        return format;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Product> getFormatInProducts() {
        return formatInProducts;
    }

    public void setFormatInProducts(List<Product> formatInProducts) {
        this.formatInProducts = formatInProducts;
    }

    public List<Product> getFormatOutProducts() {
        return formatOutProducts;
    }

    public void setFormatOutProducts(List<Product> formatOutProducts) {
        this.formatOutProducts = formatOutProducts;
    }

    //------------------------------------------------------

    public void addFormatInProduct(Product product){
        if(!formatInProducts.contains(product)) {
            formatInProducts.add(product);
            product.getFormatInList().add(this);
        }
    }

    public void removeFormatInProduct(Product product){
        formatInProducts.remove(product);
        product.getFormatInList().remove(this);
    }

    public void addFormatOutProduct(Product product){
        if(!formatOutProducts.contains(product)) {
            formatOutProducts.add(product);
            product.getFormatOutList().add(this);
        }
    }

    public void removeFormatOutProduct(Product product){
        formatOutProducts.remove(product);
        product.getFormatOutList().remove(this);
    }

    @Override
    public int compareTo(FormatVersion formatVersion) {
        return name.compareTo(formatVersion.name);
    }


}
