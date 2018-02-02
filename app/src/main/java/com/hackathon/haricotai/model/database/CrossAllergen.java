package com.hackathon.haricotai.model.database;

import java.util.List;

public class CrossAllergen {

    private String id;
    private List<String> crossAllergens;

    public CrossAllergen(String id, List<String> crossAllergens) {
        this.id = id;
        this.crossAllergens = crossAllergens;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getCrossAllergens() {
        return crossAllergens;
    }

    public void setCrossAllergens(List<String> crossAllergens) {
        this.crossAllergens = crossAllergens;
    }
}
