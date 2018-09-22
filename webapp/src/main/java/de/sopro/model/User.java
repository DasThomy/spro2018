package de.sopro.model;

import com.fasterxml.jackson.annotation.JsonView;
import de.sopro.filter.Views;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.NaturalId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Entity
public class User implements org.springframework.security.core.userdetails.UserDetails {

    public final static String ROLE_USER = "ROLE_USER";
    public final static String ROLE_ADMIN = "ROLE_ADMIN";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.BasicUser.class)
    private int id;

    @NaturalId
    @JsonView(Views.BasicUser.class)
    private String email;

    @NotBlank
    @JsonView(Views.BasicUser.class)
    private String forename;

    @NotBlank
    @JsonView(Views.BasicUser.class)
    private String surname;

    @JsonView(Views.DetailUser.class)
    private String formOfAddress;

    @JsonView(Views.DetailUser.class)
    private String title;

    @NotBlank
    private String password;

    @NotBlank
    @JsonView(Views.DetailUser.class)
    private String role;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonView(Views.DetailUser.class)
    private Organisation organisation;

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Combination> ownCombinations = new ArrayList<>();


    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Combination> sharedCombinations = new ArrayList<>();

    //------------------------------------------------------

    protected User() {
    }

    public User(String email, String forename, String surname) {
        this.email = email;
        this.forename = forename;
        this.surname = surname;
        this.role = "ROLE_USER";
    }

    //------------------------------------------------------

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFormOfAdress() {
        return formOfAddress;
    }

    public void setFormOfAdress(String formOfAdress) {
        this.formOfAddress = formOfAdress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String titel) {
        this.title = titel;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(getRole()));
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Combination> getOwnCombinations() {
        return ownCombinations;
    }


    public List<Combination> getSharedCombinations() {
        return sharedCombinations;
    }

    //------------------------------------------------------

    public void addSharedCombination(Combination combination) {
        if (!sharedCombinations.contains(combination)) {
            sharedCombinations.add(combination);
            combination.getReader().add(this);
        }
    }

    public void removeSharedCombination(Combination combination) {
        sharedCombinations.remove(combination);
        combination.getReader().remove(this);
    }

    public Combination addOwnCombination() {
        Combination combination = new Combination(this);
        ownCombinations.add(combination);
        return combination;
    }

    @Override
    public boolean equals(Object o){
        User user = (User) o;
        return user.getId() == this.getId();
    }

}
