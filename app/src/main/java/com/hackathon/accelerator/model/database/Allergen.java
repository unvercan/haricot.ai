package com.hackathon.accelerator.model.database;

public class Allergen {

    private String id;
    private String name;

    @SuppressWarnings("unused")
    public Allergen(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Allergen{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}