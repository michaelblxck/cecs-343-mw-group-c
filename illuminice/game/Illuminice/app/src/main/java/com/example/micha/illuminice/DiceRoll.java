package com.example.micha.illuminice;

import java.util.Random;

/**
 * Created by micha on 4/9/2017.
 */

public class DiceRoll {
    int die1Value;
    int die2Value;
    int diceSum;

    public DiceRoll() {
        Random rand = new Random();
        die1Value = rand.nextInt(6) + 1;
        die2Value = rand.nextInt(6) + 1;
        diceSum = die1Value + die2Value;
    }

    public int getDiceSum() {
        return diceSum;
    }
}