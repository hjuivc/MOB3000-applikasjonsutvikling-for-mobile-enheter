package com.example.recipeapp;

public class Ingredients {
    public int ingredientID, recipeID;
    public String ingredient, amount, unit;

    public Ingredients() {
        /**
         * Default konstruktør trengs for å kalle på DataSnapshot.getValue(Ingredients.class)
         */
    }

    public Ingredients(int ingredientID, int recipeID, String ingredient, String amount, String unit) {
        this.ingredientID = ingredientID;
        this.recipeID = recipeID;
        this.ingredient = ingredient;
        this.amount = amount;
        this.unit = unit;
    }
}
