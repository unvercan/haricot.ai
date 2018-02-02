package com.hackathon.haricotai.model.database;

import java.util.List;

public class Allergens {

    private String id;
    private List<Allergen> allergens;

    public Allergens(String id, List<Allergen> allergens) {
        this.id = id;
        this.allergens = allergens;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Allergen> getAllergens() {
        return allergens;
    }

    public void setAllergens(List<Allergen> allergens) {
        this.allergens = allergens;
    }

}
