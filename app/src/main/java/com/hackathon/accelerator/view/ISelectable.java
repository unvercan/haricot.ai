package com.hackathon.accelerator.view;

public interface ISelectable {

    @SuppressWarnings("unused")
    boolean isSelected();

    @SuppressWarnings("unused")
    void setIsSelected(boolean check);

    @SuppressWarnings("unused")
    String getCode();

    @SuppressWarnings("unused")
    void setCode(String id);

    @SuppressWarnings("unused")
    String getText();

    @SuppressWarnings("unused")
    void setText(String text);
}
