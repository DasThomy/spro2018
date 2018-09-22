package swarm.swarmcomposer.model;

import android.graphics.Bitmap;

public class ProductInComb {

    private int id;
    private int xPosition;
    private int yPosition;
    private Product product;

    private Bitmap logo;

    public ProductInComb(String name, int id, int xPosition, int yPosition, Product product) {

        this.id = id;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.product = product;
    }

    public ProductInComb(String name, int id, int xPosition, int posY) {

        this.id = id;
        this.xPosition = xPosition;
        this.yPosition = posY;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}