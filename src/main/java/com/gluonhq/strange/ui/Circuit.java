package com.gluonhq.strange.ui;

import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class Circuit extends Control {

    private CircuitOutput output = new CircuitOutput();

    public Circuit() {
        setPrefHeight(70);
//        setPrefWidth(Double.MAX_VALUE);
        //setStyle("-fx-background-color: white");
        getStyleClass().add("circuit");
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CircuitSkin(this);
    }

    public CircuitOutput getOutput() {
        return output;
    }
}
