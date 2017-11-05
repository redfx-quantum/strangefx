package com.gluonhq.strange.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Line;

public class CircuitSkin extends SkinBase<Circuit> {

    private Line line = new Line();
    private HBox hbox = new HBox();
    private Label label = new Label("IO>");
    private CircuitOutput output = new CircuitOutput();

    private HBox dragableArea = new HBox();

    CircuitSkin(Circuit control) {
        super(control);
        getChildren().addAll(line, hbox);

        line.getStyleClass().add("center-line");
//        line.setStyle("-fx-stroke: darkgrey;");
//        label.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-text-fill: darkgrey;");
        label.getStyleClass().add("line-label");
        hbox.getStyleClass().add("draggable-area");

//        hbox.setStyle("-fx-background-color: transparent; -fx-padding: 15 5 15 0;");
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getChildren().addAll(label, dragableArea, output);
        HBox.setHgrow(label, Priority.NEVER);
        HBox.setHgrow(dragableArea, Priority.ALWAYS);


    }


    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        line.setStartX(0);
        line.setEndX(contentWidth);
        line.setStartY(contentHeight/2);
        line.setEndY(contentHeight/2);
        hbox.resizeRelocate(0,0,contentWidth,contentHeight);
    }
}
