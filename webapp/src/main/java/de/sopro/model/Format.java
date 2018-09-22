package de.sopro.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import de.sopro.filter.Views;

import javax.naming.NameAlreadyBoundException;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class Format {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotBlank
    @JsonView(Views.BasicFormat.class)
    private String name;

    @NotNull
    @JsonView(Views.DetailFormat.class)
    private CompatibilityDegree compatibilityDegree;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval=true)
    @JsonView(Views.DetailFormat.class)
    private List<FormatVersion> versions = new ArrayList<>();

    //------------------------------------------------------

    public Format(){}

    public Format(String name,CompatibilityDegree compatibilityDegree ){
        this.name = name;
        this.compatibilityDegree = compatibilityDegree;
    }

    //------------------------------------------------------

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CompatibilityDegree getCompatibilityDegree() {
        return compatibilityDegree;
    }

    public void setCompatibilityDegree(CompatibilityDegree compatibilityDegree) {
        this.compatibilityDegree = compatibilityDegree;
    }

    public List<FormatVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<FormatVersion> versions) {
        this.versions = versions;
    }

    public void setId(int id){
        this.id = id;
    }
    //------------------------------------------------------

    /**
     *
     * @param name
     * @return
     */
    public FormatVersion addVersion(String name) {
        Optional<FormatVersion> optionalFormatVersion = versions.stream().filter(fv -> fv.getName().equals(name)).findFirst();

        if(optionalFormatVersion.isPresent()) {
            return optionalFormatVersion.get();
        } else {
            FormatVersion formatVersion = new FormatVersion(this, name);
            versions.add(formatVersion);
            return formatVersion;
        }
    }
}
