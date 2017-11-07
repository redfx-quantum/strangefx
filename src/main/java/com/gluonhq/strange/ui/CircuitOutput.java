package com.gluonhq.strange.ui;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class CircuitOutput extends Control {

    public CircuitOutput() {
        getStyleClass().add("circuit-output");
//        prefWidthProperty().bind(prefHeightProperty());
        setPrefWidth(45);
        setPrefHeight(45);

        setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CircuitOutputSkin(this);
    }

    // measuredChanceProperty
    private final DoubleProperty measuredChanceProperty = new SimpleDoubleProperty(this, "measured chance", .5) {
        @Override
        public void set(double newValue) {
            System.out.println("mcp set to "+newValue);
            if (newValue >= 0 && newValue <= 1) {
                super.set(newValue);
            }
        }
    };

    public final DoubleProperty measuredChanceProperty() {
        return measuredChanceProperty;
    }

    public final double getMeasuredChance() {
        return measuredChanceProperty.get();
    }

    public final void setMeasuredChance(double value) {
        measuredChanceProperty.set(value);
    }


}
