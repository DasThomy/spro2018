package de.sopro.DTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class FormatDTO {
    @NotBlank
    private String name;

    @NotNull
    private boolean flexible;

    private String versions = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFlexible() {
        return flexible;
    }

    public void setFlexible(boolean flexible) {
        this.flexible = flexible;
    }

    public String getVersions() {
        return versions;
    }

    public void setVersions(String versions) {
        this.versions = versions;
    }
}
