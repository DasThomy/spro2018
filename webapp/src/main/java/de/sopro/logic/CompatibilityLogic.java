package de.sopro.logic;

import de.sopro.model.Compatibility;
import de.sopro.model.CompatibilityDegree;
import de.sopro.model.FormatVersion;
import de.sopro.model.Product;
import de.sopro.repository.FormatRepository;
import de.sopro.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class CompatibilityLogic {

    private static CompatibilityLogic instance;

    @Autowired
    FormatRepository formatRepository;
    @Autowired
    ProductRepository productRepository;

//--------------------------------Singleton-Stuff-----------------------------------------------------------------------

    /**
     * Constructor
     * The complete class could work with static but in the class-diagram this one was a singleton
     */
    private CompatibilityLogic() {
    }

    /**
     * Gets an instance of CompatibilityLogic
     *
     * @return
     */
    public static CompatibilityLogic getInstance() {

        if (instance == null) {
            instance = new CompatibilityLogic();
        }

        return instance;

    }

//------------------------------------Compatibility-Stuff---------------------------------------------------------------

    /**
     * Returns the Compatibility between two Products
     *
     * @param source
     * @param target
     * @return COMPATIBLE, NOT_COMPATIBLE or COMPATIBLE_WITH_ALTERNATIVE
     */
    public Compatibility getCompatibility(Product source, Product target) {

        //constructs first level
        List<Product> productsLevel1 = getNextLevelProducts(source);
        //returns if target is found in list
        if (productsLevel1.contains(target)) {
            return Compatibility.COMPATIBLE;
        }

        //constructs second level (with 1 alternative)
        List<Product> productsLevel2 = new ArrayList<>();
        for (Product product : productsLevel1) {
            productsLevel2.addAll(getNextLevelProducts(product));
        }

        //when the target is on level deeper we return COMPATIBLE_WITH_ALTERNATIVE
        if (productsLevel2.contains(target)) {
            return Compatibility.COMPATIBLE_WITH_ALTERNATIVE;
        }


        //constructs third level (with 2 alternatives)
        List<Product> productsLevel3 = new ArrayList<>();

        for (Product product : productsLevel2) {
            productsLevel3.addAll(getNextLevelProducts(product));
        }
        //same as above
        if (productsLevel3.contains(target)) {
            return Compatibility.COMPATIBLE_WITH_ALTERNATIVE;
        }

        //if nothing else is found we return NOT_COMPATIBLE
        return Compatibility.NOT_COMPATIBLE;

    }

    /**Returns a List of all Products in which you can export from source
     *
     * @param source
     * @return List Products to export
     */
    private List<Product> getNextLevelProducts(Product source) {
        List<FormatVersion> formatOuts = source.getFormatOutList();
        List<Product> nextLevelProducts = new ArrayList<>();


        for (FormatVersion formatOut : formatOuts) {

            if(formatOut.getFormat().getCompatibilityDegree() == CompatibilityDegree.STRICT){

                List<Product> newProducts = new ArrayList<>(formatOut.getFormatInProducts());
                newProducts.removeAll(nextLevelProducts);
                nextLevelProducts.addAll(newProducts);
            }
            else{
                for (FormatVersion version : formatOut.getFormat().getVersions()) {
                    if(formatOut.compareTo(version) <= 0) {
                        List<Product> newProducts = version.getFormatInProducts();
                        newProducts.removeAll(nextLevelProducts);
                        nextLevelProducts.addAll(newProducts);
                    }

                }
            }
        }
        return nextLevelProducts;
    }


    /**
     * Checks if two Products are compatible
     *
     * @param source Product
     * @param target Product
     * @return COMPATIBLE or NOT_COMPATIBLE
     */

    public Compatibility compatibilityCheck(Product source, Product target) {
        if (getNextLevelProducts(source).contains(target)) {
            return Compatibility.COMPATIBLE;
        }
        return Compatibility.NOT_COMPATIBLE;

    }

//---------------------------Get--Alternatives--------------------------------------------------------------------------

    /**Returns a list of AlternativeProducts that would fit between two Products
     * works with trees and breadth-first search (but not recursively)
     * @param source
     * @param target
     * @return empty if NOT_COMPATIBILITY
     */
    public List<AlternativeProducts> getAlternativeProducts(Product source, Product target) {
        List<AlternativeProducts> alternativeProducts = new ArrayList<>();


        //build tree
        Node<Product> root = new Node<>(source);

        //build level one
        root.addChildren(getNextLevelProducts(source));

        List<Node<Product>> levelOneNodes = root.getLevel(1);

        for (Node<Product> productNode : levelOneNodes) {
            //build level two
            productNode.addChildren(getNextLevelProducts(productNode.getData()));
        }
        List<Node<Product>> levelTwoNodes = root.getLevel(2);

        for (Node<Product> levelTwoNode : levelTwoNodes) {
            if (levelTwoNode.getData().equals(target)) {
                AlternativeProducts alternative1 = new AlternativeProducts();
                alternative1.addProductToAlternative(levelTwoNode.getParent().getData());
                alternativeProducts.add(alternative1);


            }
        }

        if (!alternativeProducts.isEmpty()) {
            return alternativeProducts;
        }


        for (Node<Product> productNode : levelTwoNodes) {
            //build level 3
            productNode.addChildren(getNextLevelProducts(productNode.getData()));

        }


        List<Node<Product>> levelThreeNodes = root.getLevel(3);

        for (Node<Product> levelThreeNode : levelThreeNodes) {
            if (levelThreeNode.getData().equals(target)) {

                AlternativeProducts alternative1 = new AlternativeProducts();
                alternative1.addProductToAlternative(levelThreeNode.getParent().getParent().getData());
                alternative1.addProductToAlternative(levelThreeNode.getParent().getData());
                alternativeProducts.add(alternative1);

            }
        }


        return alternativeProducts;
    }


}
