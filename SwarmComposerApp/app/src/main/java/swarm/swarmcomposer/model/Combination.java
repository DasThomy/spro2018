package swarm.swarmcomposer.model;

import java.util.ArrayList;

public class Combination {
    private ArrayList<Connection> connections;
    private ArrayList<ProductInComb> productsInComb;
    private String name;

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    private int sizeX;
    private int sizeY;
    private String email;
    private int id;

    public Combination() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Combination(ArrayList<Connection> cl, ArrayList<ProductInComb> picl, String name, int sizeX, int sizeY, String email) {
        this.connections = cl;
        this.productsInComb = picl;
        this.name = name;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.email = email;
    }

    public ArrayList<Connection> getConnections() {
        return connections;
    }

    public void setConnections(ArrayList<Connection> connections) {
        this.connections = connections;
    }

    public ArrayList<ProductInComb> getProductsInComb() {
        return productsInComb;
    }

    public void setProductsInComb(ArrayList<ProductInComb> productsInComb) {
        this.productsInComb = productsInComb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}