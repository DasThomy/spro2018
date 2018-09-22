package swarm.swarmcomposer.helper;

/**
 * A List Element stores the Name and an Id of a product or combination
 */
public class ListElement {

    private String name;
    private int id;

    public ListElement(String name, int ID) {
        this.name = name;
        this.id = ID;
    }

    public ListElement() {

    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
