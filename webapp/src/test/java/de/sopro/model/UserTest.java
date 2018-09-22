package de.sopro.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {

    @Test
    void addSharedCombination() {
        User user = new User();
        Combination combination = new Combination();
        user.addSharedCombination(combination);
        assertThat(user.getSharedCombinations()).contains(combination);
        assertThat(combination.getReader()).contains(user);
        //add a second time
        user.addSharedCombination(combination);
        assertThat(user.getSharedCombinations()).containsOnlyOnce(combination);
        assertThat(combination.getReader()).containsOnlyOnce(user);
    }


    @Test
    void removeSharedCombination() {
        User user = new User();
        Combination combination = new Combination();
        user.addSharedCombination(combination);
        user.removeSharedCombination(combination);
        assertThat(user.getSharedCombinations()).doesNotContain(combination);
        assertThat(combination.getReader()).doesNotContain(user);
    }

    @Test
    void addOwnCombination() {
        User user = new User();
        Combination combination = user.addOwnCombination();
        assertThat(combination.getCreator()).isEqualTo(user);
        assertThat(user.getOwnCombinations()).contains(combination);
    }
}