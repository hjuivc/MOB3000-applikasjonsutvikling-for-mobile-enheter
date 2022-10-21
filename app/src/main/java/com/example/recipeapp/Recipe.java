package com.example.recipeapp;

public class Recipe {
    public int recipeID;
    public String userID;
    public String name;
    public String description;
    public String stepbystep;
    public String cuisine;
    public Boolean vegan;
    public Boolean favorite;

    public Recipe() {
        /**
         * Default konstruktør trengs for å kalle på DataSnapshot.getValue(Recipe.class)
         */
    }

    public Recipe(String userID, int recipeID, String name, String description, String stepbystep, String cuisine, Boolean vegan, Boolean favorite) {
        this.userID = userID;
        this.recipeID = recipeID;
        this.name = name;
        this.description = description;
        this.stepbystep = stepbystep;
        this.cuisine = cuisine;
        this.vegan = vegan;
        this.favorite = favorite;
    }
}
