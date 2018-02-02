package com.hackathon.haricotai.model.database;

import java.io.Serializable;
import java.util.List;

public class Food implements Serializable {

    private String id;
    private String name;
    private List<Ingredient> ingredients;
    private List<Ingredient> detectedAllergens;

    public Food(String id, String name, List<Ingredient> ingredients, List<Ingredient> detectedAllergens) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.detectedAllergens = detectedAllergens;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<Ingredient> getDetectedAllergens() {
        return detectedAllergens;
    }

    public void setDetectedAllergens(List<Ingredient> detectedAllergens) {
        this.detectedAllergens = detectedAllergens;
    }
}
