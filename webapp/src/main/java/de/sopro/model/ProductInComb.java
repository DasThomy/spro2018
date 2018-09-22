package de.sopro.model;

import com.fasterxml.jackson.annotation.*;
import de.sopro.filter.Views;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
public class ProductInComb {

    @Id
    @JsonView(Views.DetailCombination.class)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    @Min(0)
    @JsonView(Views.DetailCombination.class)
    @NonNull
    private int xPosition;

    @Min(0)
    @JsonView(Views.DetailCombination.class)
    @NonNull
    private int yPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @NonNull
    @JsonView(Views.DetailCombination.class)
    private Product product;

    //------------------------------------------------------
    //------------------------------------------------------

    protected ProductInComb(){}

    ProductInComb(Product product){
        this.product = product;
    }

    //------------------------------------------------------

    public int getId(){
        return id;
    }

    public int getxPosition() {
        return xPosition;
    }

    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    public Product getProduct() {
        return this.product;
    }
}
