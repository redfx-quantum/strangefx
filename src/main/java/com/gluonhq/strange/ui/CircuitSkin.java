package com.gluonhq.strange.ui;

import com.gluonhq.strange.Model;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
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

    private HBox dragableArea = new HBox();
    private Circuit circuit;

    CircuitSkin(Circuit control) {
        super(control);
        this.circuit = control;
        getChildren().addAll(line, hbox);

        line.getStyleClass().add("center-line");
        label.getStyleClass().add("line-label");
        hbox.getStyleClass().add("draggable-area");

        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getChildren().addAll(label, dragableArea, getSkinnable().getOutput());
        HBox.setHgrow(label, Priority.NEVER);
        HBox.setHgrow(dragableArea, Priority.ALWAYS);
        circuit.getGates().addListener((Observable observable) -> {
            List<Gate> gates = circuit.getGates();
            Model.getInstance().setGatesForQubit(circuit.getIndex(), gates);
            System.out.println("GATES for qubit "+circuit.getIndex()+": "+gates);
        });

    }


    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        line.setStartX(0);
        line.setEndX(contentWidth);
        line.setStartY(contentHeight / 2);
        line.setEndY(contentHeight / 2);
        hbox.resizeRelocate(0, 0, contentWidth, contentHeight);
    }
}
