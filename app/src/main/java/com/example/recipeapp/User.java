package com.example.recipeapp;

public class User {
    public String fullName, age, email;

    public User() {
        /**
         * Default konstruktør trengs for å kalle på DataSnapshot.getValue(User.class)
          */
    }

    public User(String fullName, String age, String email) {
        this.fullName = fullName;
        this.age = age;
        this.email = email;
    }
}
