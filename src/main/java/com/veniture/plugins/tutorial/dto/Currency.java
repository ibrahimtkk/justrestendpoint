package com.veniture.plugins.tutorial.dto;

public class Currency {
    String name;
    Float unit;

    public Currency(String name, Float unit) {
        this.name = name;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getUnit() {
        return unit;
    }

    public void setUnit(Float unit) {
        this.unit = unit;
    }
}