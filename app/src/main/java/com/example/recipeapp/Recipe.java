package com.example.recipeapp;

public class Recipe {
    public String name, description, stepbystep, cuisine, vegan, favorite;

    public Recipe() {
        /**
         * Default konstruktør trengs for å kalle på DataSnapshot.getValue(Recipe.class)
         */
    }

    public Recipe(String name, String description, String stepbystep, String cuisine, String vegan, String favorite) {
        this.name = name;
        this.description = description;
        this.stepbystep = stepbystep;
        this.cuisine = cuisine;
        this.vegan = vegan;
        this.favorite = favorite;
    }
}
