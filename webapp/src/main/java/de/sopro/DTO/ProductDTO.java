package de.sopro.DTO;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

public class ProductDTO {

    @NotBlank
    private String name;

    private String organisation;

    private String version;

    private long releaseDate;

    private String tags = "";

    private List<String> formatInList = new ArrayList<>();

    private List<String> formatOutList = new ArrayList<>();

    private boolean certified = false;

    public void setCertified(boolean certified) {
        this.certified = certified;
    }

    public boolean getCertified() {
        return certified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(long releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<String> getFormatInList() {
        return formatInList;
    }

    public void setFormatInList(List<String> formatInList) {
        this.formatInList = formatInList;
    }

    public List<String> getFormatOutList() {
        return formatOutList;
    }

    public void setFormatOutList(List<String> formatOutList) {
        this.formatOutList = formatOutList;
    }
}

