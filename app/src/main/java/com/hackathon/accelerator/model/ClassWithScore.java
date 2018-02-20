package com.hackathon.accelerator.model;

import java.io.Serializable;

public class ClassWithScore implements Serializable {

    private String name;
    private float score;

    public ClassWithScore(String name, float score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return this.name;
    }

    public float getScore() {
        return this.score;
    }

    @Override
    public String toString() {
        return "ClassWithScore{" +
                "name='" + name + '\'' +
                ", score=" + score +
                '}';
    }
}
