package com.hackathon.accelerator.model.database;

import java.util.List;

public class Allergens {

    private String _id;
    private String _rev;
    private List<Allergen> allergen_list;

    public Allergens(String id, String rev, List<Allergen> allergen_list) {
        this._id = id;
        this._rev = rev;
        this.allergen_list = allergen_list;
    }

    public List<Allergen> getAllergen_list() {
        return allergen_list;
    }

    @Override
    public String toString() {
        return "Allergens{" +
                "_id='" + _id + '\'' +
                ", _rev='" + _rev + '\'' +
                ", allergen_list=" + allergen_list +
                '}';
    }
}