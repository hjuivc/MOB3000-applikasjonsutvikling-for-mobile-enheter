package com.example.recipeapp;

public class Ingredients {
    public int ingredientID, recipeID;
    public String ingredient, amount, unit;

    public Ingredients() {
        // Default constructor needed to call DataSnapshot.getValue(Ingredients.class)
    }

    public Ingredients(int ingredientID, int recipeID, String ingredient, String amount, String unit) {
        this.ingredientID = ingredientID;
        this.recipeID = recipeID;
        this.ingredient = ingredient;
        this.amount = amount;
        this.unit = unit;
    }

    public int getIngredientID() {
        return ingredientID;
    }

    public void setIngredientID(int ingredientID) {
        this.ingredientID = ingredientID;
    }

    public int getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(int recipeID) {
        this.recipeID = recipeID;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
