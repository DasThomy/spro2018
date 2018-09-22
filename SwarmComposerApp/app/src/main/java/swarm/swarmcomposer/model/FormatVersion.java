package swarm.swarmcomposer.model;

public class FormatVersion {
    public FormatVersion() {
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    String name;
    Format format;
}
