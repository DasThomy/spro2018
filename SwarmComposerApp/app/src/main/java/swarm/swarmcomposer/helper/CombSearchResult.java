package swarm.swarmcomposer.helper;

import java.util.ArrayList;

/**
 * Class to share all different types of Combination for the Search engine
 */
public class CombSearchResult {
    private ArrayList<ListElement> publicCombinationList = new ArrayList<>();
    private ArrayList<ListElement> sharedCombinationsList = new ArrayList<>();
    private ArrayList<ListElement> ownCombinationsList = new ArrayList<>();


    public CombSearchResult() {

    }

    public ArrayList<ListElement> getPublicCombinationList() {
        return publicCombinationList;
    }

    public void setPublicCombinationList(ArrayList<ListElement> publicCombinationList) {
        this.publicCombinationList = publicCombinationList;
    }

    public ArrayList<ListElement> getSharedCombinationsList() {
        return sharedCombinationsList;
    }

    public void setSharedCombinationsList(ArrayList<ListElement> sharedCombinationsList) {
        this.sharedCombinationsList = sharedCombinationsList;
    }

    public ArrayList<ListElement> getOwnCombinationsList() {
        return ownCombinationsList;
    }

    public void setOwnCombinationsList(ArrayList<ListElement> ownCombinationsList) {
        this.ownCombinationsList = ownCombinationsList;
    }
}
