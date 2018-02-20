package com.hackathon.accelerator.model.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Food implements Serializable {

    private String _id;
    private String _rev;
    private String food_name;
    private List<Ingredient> ingredient;
    private List<Ingredient> detectedAllergens = new ArrayList<>();

    public Food(String _id, String _rev, String food_name, List<Ingredient> ingredient, List<Ingredient> detectedAllergens) {
        this._id = _id;
        this._rev = _rev;
        this.food_name = food_name;
        this.ingredient = ingredient;
        this.detectedAllergens = detectedAllergens;
    }

    public List<Ingredient> getIngredient() {
        return ingredient;
    }

    public String getFood_name() {
        return food_name;
    }

    public List<Ingredient> getDetectedAllergens() {
        return detectedAllergens;
    }

    public void setDetectedAllergens(List<Ingredient> detectedAllergens) {
        this.detectedAllergens = detectedAllergens;
    }


    @Override
    public String toString() {
        return "Food{" +
                "_id='" + _id + '\'' +
                ", _rev='" + _rev + '\'' +
                ", food_name='" + food_name + '\'' +
                ", ingredient=" + ingredient +
                ", detectedAllergens=" + detectedAllergens +
                '}';
    }
}