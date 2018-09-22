package de.sopro.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrganisationTest {

    @Test
    void addSharedCombination() {
        Organisation organisation = new Organisation("test orga");
        Combination combination = new Combination();
        organisation.addSharedCombination(combination);
        assertThat(organisation.getSharedCombinations()).contains(combination);
    }

    @Test
    void addSharedCombinationTwice() {
        Organisation organisation = new Organisation("test orga");
        Combination combination = new Combination();
        organisation.addSharedCombination(combination);
        organisation.addSharedCombination(combination);
        assertThat(organisation.getSharedCombinations()).containsOnlyOnce(combination);
    }

    @Test
    void removeSharedCombination() {
        Organisation organisation = new Organisation("test orga");
        Combination combination = new Combination();
        organisation.addSharedCombination(combination);
        organisation.removeSharedCombination(combination);
        assertThat(organisation.getSharedCombinations()).doesNotContain(combination);
    }
}