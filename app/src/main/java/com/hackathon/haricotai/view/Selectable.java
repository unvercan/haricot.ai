package com.hackathon.haricotai.view;

public interface Selectable {

    boolean isSelected();

    void setIsSelected(boolean check);

    String getCode();

    void setCode(String id);

    String getText();

    void setText(String text);
}
