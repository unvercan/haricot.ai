package com.hackathon.accelerator.model.database;

import java.util.List;

public class CrossAllergen {

    private String _id;
    private String _rev;
    private List<String> cross_allergen;

    public CrossAllergen(String _id, String _rev, List<String> cross_allergen) {
        this._id = _id;
        this._rev = _rev;
        this.cross_allergen = cross_allergen;
    }

    public List<String> getCrossAllergen() {
        return cross_allergen;
    }


    @Override
    public String toString() {
        return "CrossAllergen{" +
                "_id='" + _id + '\'' +
                ", _rev='" + _rev + '\'' +
                ", cross_allergen=" + cross_allergen +
                '}';
    }
}