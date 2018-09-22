package swarm.swarmcomposer.model;

public class Format {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isCompabilityDegree() {
        return compabilityDegree;
    }

    public void setCompabilityDegree(boolean compabilityDegree) {
        this.compabilityDegree = compabilityDegree;
    }

    public Format() {

    }

    private String name;
    private boolean compabilityDegree;

}