package com.example.recipeapp;

public class Ingredients {
    public String ingredient, amount, unit;

    public Ingredients() {
        /**
         * Default konstruktør trengs for å kalle på DataSnapshot.getValue(Ingredients.class)
         */
    }

    public Ingredients(String ingredient, String amount, String unit) {
        this.ingredient = ingredient;
        this.amount = amount;
        this.unit = unit;
    }
}
