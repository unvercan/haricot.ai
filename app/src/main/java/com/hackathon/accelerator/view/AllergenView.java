package com.hackathon.accelerator.view;

import com.hackathon.accelerator.model.database.Allergen;

public class AllergenView implements ISelectable {

    private Allergen allergen;
    private boolean selected;

    public AllergenView(Allergen allergen) {
        this.allergen = allergen;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setIsSelected(boolean check) {
        selected = check;
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
