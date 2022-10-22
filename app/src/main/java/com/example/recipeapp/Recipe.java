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

    public int getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(int recipeID) {
        this.recipeID = recipeID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStepbystep() {
        return stepbystep;
    }

    public void setStepbystep(String stepbystep) {
        this.stepbystep = stepbystep;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public Boolean getVegan() {
        return vegan;
    }

    public void setVegan(Boolean vegan) {
        this.vegan = vegan;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }
}
