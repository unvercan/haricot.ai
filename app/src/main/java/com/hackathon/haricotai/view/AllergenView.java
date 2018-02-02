package com.hackathon.haricotai.view;

import com.hackathon.haricotai.model.database.Allergen;

public class AllergenView implements Selectable {

    private Allergen allergen;
    private boolean isSelected;

    public AllergenView(Allergen allergen) {
        this.allergen = allergen;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setIsSelected(boolean check) {
        isSelected = check;
    }

    @Override
    public String getCode() {
        return this.allergen.getId();
    }

    @Override
    public void setCode(String id) {
        this.allergen.setId(id);
    }

    @Override
    public String getText() {
        return this.allergen.getName();
    }

    @Override
    public void setText(String text) {
        this.allergen.setName(text);
    }

    public Allergen getAllergen() {
        return allergen;
    }
}