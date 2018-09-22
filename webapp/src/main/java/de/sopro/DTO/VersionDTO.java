package de.sopro.DTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class VersionDTO {

    private String versions = "";

    public String getVersions() {
        return versions;
    }

    public void setVersions(String versions) {
        this.versions = versions;
    }
}
