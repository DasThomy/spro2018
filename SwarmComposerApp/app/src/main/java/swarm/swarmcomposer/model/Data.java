package swarm.swarmcomposer.model;

import java.util.ArrayList;

import swarm.swarmcomposer.helper.CombSearchResult;
import swarm.swarmcomposer.helper.ListElement;

public class Data {

    private String email = "";
    private String serverAddresse;
    private int fontSizeNormalText = 25;
    private ArrayList<Combination> combinationList;
    private ArrayList<Product> productList;
    private ArrayList<ListElement> allProducts;
    private ArrayList<ListElement> allCombination;
    private ArrayList<ListElement> allCombinationOwn;
    private ArrayList<ListElement> allCombinationPublic;
    private ArrayList<ListElement> allCombinationSearched;
    private ArrayList<ListElement> searchProducts;
    private ArrayList<ListElement> allCombinationShared;

    private CombSearchResult combSearchResult;

    private Boolean loggedIn = false;

    public int getFontSizeNormalText() {
        return fontSizeNormalText;
    }

    public void setFontSizeNormalText(int fontSizeNormalText) {
        this.fontSizeNormalText = fontSizeNormalText;
    }

    public Data() {
        combinationList = new ArrayList<>();
        productList = new ArrayList<>();
        allProducts = new ArrayList<>();
        allCombination = new ArrayList<>();
        allCombinationOwn = new ArrayList<>();
        allCombinationPublic = new ArrayList<>();
        allCombinationSearched = new ArrayList<>();
        searchProducts = new ArrayList<>();
        allCombinationShared = new ArrayList<>();
    }

    public void addCombination(Combination combination) {
        for (Combination iter : combinationList) {
            if (iter.getId() == combination.getId()) {
                combinationList.remove(iter);
                break;
            }
        }
        combinationList.add(combination);
    }

    public void addProduct(Product product) {
        productList.add(product);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getServerAddresse() {
        return serverAddresse;
    }

    public void setServerAddresse(String serverAddresse) {
        this.serverAddresse = serverAddresse;
    }

    public ArrayList<Combination> getCombinationList() {
        return combinationList;
    }

    public void setCombinationList(ArrayList<Combination> combinationList) {
        this.combinationList = combinationList;
    }

    public ArrayList<Product> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<Product> productList) {
        this.productList = productList;
    }

    public ArrayList<ListElement> getAllProducts() {
        return allProducts;
    }

    public void setAllProducts(ArrayList<ListElement> allProducts) {
        this.allProducts = allProducts;
    }

    public ArrayList<ListElement> getAllCombination() {
        return allCombination;
    }

    public void setAllCombination(ArrayList<ListElement> allCombination) {
        this.allCombination = allCombination;
    }

    public ArrayList<ListElement> getAllCombinationOwn() {
        return allCombinationOwn;
    }

    public void setAllCombinationOwn(ArrayList<ListElement> allCombinationOwn) {
        this.allCombinationOwn = allCombinationOwn;
    }

    public ArrayList<ListElement> getAllCombinationShared() {
        return allCombinationShared;
    }

    public void setAllCombinationShared(ArrayList<ListElement> allCombinationShared) {
        this.allCombinationShared = allCombinationShared;
    }

    public ArrayList<ListElement> getAllCombinationPublic() {
        return allCombinationPublic;
    }

    public void setAllCombinationPublic(ArrayList<ListElement> allCombinationPublic) {
        this.allCombinationPublic = allCombinationPublic;
    }

    public ArrayList<ListElement> getSearchProducts() {
        return searchProducts;
    }

    public void setSearchProducts(ArrayList<ListElement> searchProducts) {
        this.searchProducts = searchProducts;
    }

    public ArrayList<ListElement> getAllCombinationSearched() {
        return allCombinationSearched;
    }

    public void setAllCombinationSearched(ArrayList<ListElement> allCombinationSearched) {
        this.allCombinationSearched = allCombinationSearched;
    }

    public CombSearchResult getCombSearchResult() {
        return combSearchResult;
    }

    public void setCombSearchResult(CombSearchResult combSearchResult) {
        this.combSearchResult = combSearchResult;
    }

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}