package de.sopro.model;

import com.fasterxml.jackson.annotation.JsonView;
import de.sopro.filter.Views;
import org.springframework.data.annotation.CreatedBy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Combination {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.BasicCombination.class)
    private int id;

    @NotBlank
    @JsonView(Views.BasicCombination.class)
    private String name;

    @NotNull
    @JsonView(Views.DetailCombination.class)
    private boolean publicVisible;

    @NotNull
    @ManyToOne
    @CreatedBy
    private User creator;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JsonView(Views.DetailCombination.class)
    private List<User> reader = new ArrayList<>();

    @JsonView(Views.DetailCombination.class)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval=true)
    private List<Connection> connections = new ArrayList<>();

    @JsonView(Views.DetailCombination.class)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval=true)
    private List<ProductInComb> productsInComb = new ArrayList<>();

    //------------- Constructor ----------------

    public Combination(){
    }

    protected Combination(User creator){
        this.creator = creator;
        this.publicVisible = false;
    }

    //------------- Getter/ Setter -------------
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPublicVisible() {
        return publicVisible;
    }

    public void setPublicVisible(boolean publicVisible) {
        this.publicVisible = publicVisible;
    }

    public User getCreator() {
        return creator;
    }

    public List<User> getReader() {
        return reader;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void setConnections(List<Connection> connections) {
        if(connections != null) {
            this.connections = connections;
        }
    }

    public List<ProductInComb> getProductsInComb() {
        return productsInComb;
    }

    //------------------------------------------------------


    public void addReadPermission(User user){
        if(!reader.contains(user)) {
            reader.add(user);
            user.getSharedCombinations().add(this);
        }
    }

    public void removeReadPermission(User user){
        reader.remove(user);
        user.getSharedCombinations().remove(this);
    }

    public Connection addConnection(ProductInComb sourceProduct, ProductInComb targetProduct) throws IllegalArgumentException{
        for(Connection connection : connections){
            if(connection.getSourceProduct() ==sourceProduct && connection.getTargetProduct() == targetProduct){
                return null;
            }
        }
        Connection con = new Connection(sourceProduct, targetProduct);
        connections.add(con);
        return con;
    }

    public void removeConnection(Connection connection){
        connections.remove(connection);
    }

    public ProductInComb addProductInComb(Product product){
        ProductInComb productInComb = new ProductInComb(product);
        productsInComb.add(productInComb);
        return productInComb;
    }

    public void removeProductInComb(ProductInComb productInComb){
        productsInComb.remove(productInComb);
        for(Connection conn : connections){
            if(conn.getSourceProduct() == productInComb || conn.getTargetProduct() == productInComb){
                removeConnection(conn);
            }
        }
    }

    @PreRemove
    private void removeCombinationFromUsers() {
        for (User u : reader) {
            u.getSharedCombinations().remove(this);
        }
    }

    public void setProductsInComb(List<ProductInComb> productsInComb) {
        this.productsInComb = productsInComb;
    }
}
