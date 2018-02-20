package com.hackathon.accelerator.model.nlp;

import java.util.List;

public class ContextData {
    private String food;
    private List<String> allIngredients;
    private List<String> allergenIngredients;

    public ContextData(String food, List<String> allIngredients, List<String> allergenIngredients) {
        this.food = food;
        this.allIngredients = allIngredients;
        this.allergenIngredients = allergenIngredients;
    }

    public String getFood() {
        return food;
    }

    public List<String> getAllIngredients() {
        return allIngredients;
    }

    public List<String> getAllergenIngredients() {
        return allergenIngredients;
    }

    @Override
    public String toString() {
        return "ContextData{" +
                "food='" + food + '\'' +
                ", allIngredients=" + allIngredients +
                ", allergenIngredients=" + allergenIngredients +
                '}';
    }
}
