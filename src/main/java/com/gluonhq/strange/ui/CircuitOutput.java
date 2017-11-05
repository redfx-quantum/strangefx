package com.gluonhq.strange.ui;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class CircuitOutput extends Control {

    public CircuitOutput() {
        getStyleClass().add("circuit-output");
        setPrefWidth(45);
//        prefWidthProperty().bind(prefHeightProperty());
//        setStyle("-fx-background-color: white; -fx-border-color: darkgrey; -fx-border-width: 1;");
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CircuitOutputSkin(this);
    }

    // stateProperty
    private final DoubleProperty stateProperty = new SimpleDoubleProperty(this, "state", .5) {
        @Override
        public void set(double newValue) {
            if ( newValue >= 0 && newValue <= 1) {
                super.set(newValue);
            }
        }
    };
    public final DoubleProperty stateProperty() {
       return stateProperty;
    }
    public final double getState() {
       return stateProperty.get();
    }
    public final void setState(double value) {
        stateProperty.set(value);
    }


}
