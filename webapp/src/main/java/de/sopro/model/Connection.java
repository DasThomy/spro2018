package de.sopro.model;

import com.fasterxml.jackson.annotation.*;
import de.sopro.filter.Views;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Connection {

    @Id
    @JsonView(Views.DetailCombination.class)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull
    @JsonView(Views.DetailCombination.class)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @ManyToOne
    private ProductInComb sourceProduct;

    @NotNull
    @JsonView(Views.DetailCombination.class)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @ManyToOne
    private ProductInComb targetProduct;

    @JsonView(Views.DetailCombination.class)
    private Compatibility compatibility;

    //------------------------------------------------------

    protected Connection(){}

    Connection(ProductInComb sourceProduct, ProductInComb targetProduct){
        this.sourceProduct = sourceProduct;
        this.targetProduct = targetProduct;
        //TODO TEST with saving of Kombinations and stuff!!!
       // this.compatibility = CompatibilityLogic.getInstance().getCompatibility(sourceProduct.getProduct(), targetProduct.getProduct());
    }

    //------------------------------------------------------

    public int getID(){
        return id;
    }

    public Compatibility getCompatibility() {
        return compatibility;
    }

    public void setCompatibility(Compatibility compatibility) {
        this.compatibility = compatibility;
    }

    public ProductInComb getSourceProduct() {
        return sourceProduct;
    }

    public ProductInComb getTargetProduct() {
        return targetProduct;
    }

    
    public void setSourceProduct(ProductInComb sourceProduct) {
        this.sourceProduct = sourceProduct;
    }


    public void setTargetProduct(ProductInComb targetProduct) {
        this.targetProduct = targetProduct;
    }
}
