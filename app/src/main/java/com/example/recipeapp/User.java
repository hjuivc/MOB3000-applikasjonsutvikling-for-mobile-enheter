package com.example.recipeapp;

public class User {
    public String fullName;
    public String age;
    public String email;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String fullName, String age, String email) {
        this.fullName = fullName;
        this.age = age;
        this.email = email;
    }
}
