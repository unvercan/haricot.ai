package com.hackathon.accelerator.model.database;

import java.io.Serializable;

public class Ingredient implements Serializable {

    private String id;
    private String name;

    @SuppressWarnings("unused")
    public Ingredient(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}