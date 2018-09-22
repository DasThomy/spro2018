package de.sopro.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.sopro.filter.Views;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Product {

    @Id
    @JsonView(Views.BasicProduct.class)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NonNull
    @JsonView(Views.BasicProduct.class)
    private String name;

    @JsonView(Views.DetailProduct.class)
    private boolean certified;

    @JsonView(Views.DetailProduct.class)
    private String organisation;

    @JsonView(Views.DetailProduct.class)
    private String version;

    @JsonView(Views.DetailProduct.class)
    private long releaseDate;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JsonView(Views.DetailProduct.class)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
    @JsonIdentityReference(alwaysAsId = true)
    private List<Tag> tags = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JsonView(Views.DetailProduct.class)
    private List<FormatVersion> formatInList = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JsonView(Views.DetailProduct.class)
    private List<FormatVersion> formatOutList = new ArrayList<>();

    private String logoName;

    @Lob
    @JsonView(Views.BasicProduct.class)
    private String picture;

    //------------------------------------------------------

    public Product() {
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

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<FormatVersion> getFormatInList() {
        return formatInList;
    }

    public void setFormatInList(List<FormatVersion> formatInList) {
        this.formatInList = formatInList;
    }

    public List<FormatVersion> getFormatOutList() {
        return formatOutList;
    }

    public void setFormatOutList(List<FormatVersion> formatOutList) {
        this.formatOutList = formatOutList;
    }

    public boolean isCertified() {
        return certified;
    }

    public void setCertified(boolean certified) {
        this.certified = certified;
    }

    public String getLogoName() {
        return logoName;
    }

    public void setLogoName(String logoName) {
        this.logoName = logoName;
    }

    public String getPicture() {
        if (picture == null) {
            return " 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";
        }
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
//------------------------------------------------------

    public void addFormatIn(FormatVersion formatIn) {
        if (!formatInList.contains(formatIn)) {
            formatInList.add(formatIn);
            formatIn.getFormatInProducts().add(this);
        }
    }

    public void removeFormatIn(FormatVersion formatIn) {
        formatInList.remove(formatIn);
        formatIn.getFormatInProducts().remove(this);
    }

    public void addFormatOut(FormatVersion formatOut) {
        if (!formatOutList.contains(formatOut)) {
            formatOutList.add(formatOut);
            formatOut.getFormatOutProducts().add(this);
        }
    }

    public void removeFormatOut(FormatVersion formatOut) {
        formatOutList.remove(formatOut);
        formatOut.getFormatOutProducts().remove(this);
    }

    public void addTag(Tag tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
            tag.getProductsWithTag().add(this);
        }
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getProductsWithTag().remove(this);
    }

    @PreRemove
    private void removeProductFromFormatsAndTags() {
        for (Tag tag : tags) {
            tag.getProductsWithTag().remove(this);
        }
        for (FormatVersion format : formatInList) {
            format.getFormatInProducts().remove(this);
        }
        for (FormatVersion format : formatOutList) {
            format.getFormatOutProducts().remove(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        Product product = (Product) o;
        return product.getId() == this.getId();
    }

    @Override
    public String toString() {
        return (id + " " + name);
    }
}
