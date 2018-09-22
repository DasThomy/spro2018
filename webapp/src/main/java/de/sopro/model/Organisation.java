package de.sopro.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Organisation {
    @Id
    private String name;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Combination> sharedCombinations = new ArrayList<>();

    protected Organisation(){}

    public Organisation(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addSharedCombination(Combination combination){
        if(!sharedCombinations.contains(combination)){
            sharedCombinations.add(combination);
        }
    }

    public void removeSharedCombination(Combination combination){
        sharedCombinations.remove(combination);
    }

    public List<Combination> getSharedCombinations() {
        return sharedCombinations;
    }
}
