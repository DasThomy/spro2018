package swarm.swarmcomposer.model;

import android.util.Log;

import swarm.swarmcomposer.helper.Compatibility;

public class Connection {

    private ProductInComb productStart;
    private ProductInComb productTarget;
    private int sourceProduct;
    private int targetProduct;
    private Compatibility compatibility = Compatibility.COMPATIBLE;

    public Connection(ProductInComb start, ProductInComb target, Compatibility status) {
        this.productStart = start;
        this.productTarget = target;
        if (status != null)
            this.compatibility = status;
        Log.i("BLA", "CONS");

    }

    public ProductInComb getProductStart() {
        return productStart;
    }

    public void setProductStart(ProductInComb productStart) {
        this.productStart = productStart;
    }

    public ProductInComb getProductTarget() {
        return productTarget;
    }

    public void setProductTarget(ProductInComb productTarget) {
        this.productTarget = productTarget;
    }

    public Compatibility getStatus() {
        return compatibility;
    }

    public void setStatus(Compatibility status) {
        Log.i("BLA", "SET");
        if (status != null)
            this.compatibility = status;
    }

    public int getSourceProduct() {
        return sourceProduct;
    }

    public void setSourceProduct(int sourceProduct) {
        this.sourceProduct = sourceProduct;
    }

    public int getTargetProduct() {
        return targetProduct;
    }

    public void setTargetProduct(int targetProduct) {
        this.targetProduct = targetProduct;
    }
}