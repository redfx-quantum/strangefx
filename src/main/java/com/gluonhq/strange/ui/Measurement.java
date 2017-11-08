package com.gluonhq.strange.ui;

import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class Measurement extends Region {

    private Label label = new Label();
    private Pane  progress = new Pane();

    public Measurement() {

        getStyleClass().add("measurement");

        BorderPane progressBase = new BorderPane();
        progressBase.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        progressBase.setBottom(progress);
        progress.setPrefHeight(0);
        progress.getStyleClass().add("progress");
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        StackPane stack = new StackPane(progressBase, label);
        stack.prefWidthProperty().bind(widthProperty());
        stack.prefHeightProperty().bind(heightProperty());

        getChildren().add(stack);

        prefWidthProperty().bind(heightProperty());

        updateMeasuredChance();
        measuredChanceProperty.addListener( (Observable o) -> updateMeasuredChance());

    }

    private void updateMeasuredChance() {
        label.setText( measuredChanceAsString());
        progress.setPrefHeight( getHeight() * getMeasuredChance() );
    }

    private String measuredChanceAsString() {
        double chance = getMeasuredChance();
        if ( chance == 0d) return "Off";
        if (chance == 1d) return "On";
        return String.format("%.2f%%", chance * 100);
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
