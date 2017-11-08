package com.gluonhq.strange.ui;

import javafx.beans.Observable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;

import java.util.stream.Collectors;

public class CircuitSkin extends SkinBase<Circuit> {

    private Line line = new Line();
    private BorderPane content = new BorderPane();
    private Label label = new Label("IO>");

    private HBox gateRow = new HBox();
    private Circuit circuit;

    CircuitSkin(Circuit control) {
        super(control);
        this.circuit = control;
        getChildren().addAll(line, content);

        line.getStyleClass().add("center-line");
        label.getStyleClass().add("line-label");
        gateRow.getStyleClass().add("gate-row");

        content.setStyle("-fx-padding: 10 5 10 0");
        content.setLeft(label);
        content.setCenter(gateRow);
        content.setRight(getSkinnable().getOutput());
        BorderPane.setAlignment(label, Pos.CENTER_LEFT);
        BorderPane.setAlignment(getSkinnable().getOutput(), Pos.CENTER_LEFT);
        BorderPane.clearConstraints(gateRow);

        // initial update from control's gates
        gateRow.getChildren().setAll(control.getGateSymbols());

        //ensure all updates from the skin go back to control
        gateRow.getChildren().addListener( (Observable observable) -> {
            control.getGateSymbols().setAll(
                gateRow.getChildren().stream().map( n -> (GateSymbol)n).collect(Collectors.toList())
            );
        });


        gateRow.setOnDragOver(event -> {

            if (event.getGestureSource() != this &&

                // only accept gates
                event.getDragboard().getContent(GateSymbol.DRAGGABLE_GATE) != null) {

                // allow both copy(i.e. creation from toolbar) and move(between circuits)
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }

            event.consume();

        });

        gateRow.setOnDragDropped(e -> {

            Dragboard db = e.getDragboard();
            if ( db.hasContent(GateSymbol.DRAGGABLE_GATE) ) {

                // retriave symbol from global storage (only way to keep ref to the node)
                GateSymbol symbol = (GateSymbol) System.getProperties().get(GateSymbol.DRAGGABLE_GATE);
                if ( TransferMode.MOVE == e.getTransferMode()) {

                    // move the gate symbol between circuits
                    symbol.removeFromParent();
                    gateRow.getChildren().add(symbol);
                    e.setDropCompleted(true);

                } else {

                    // re-create gate symbol which was dragged from the toolbar
                    gateRow.getChildren().add(GateSymbol.of(symbol.getGate()));
                    e.setDropCompleted(true);
                }

            }
            e.consume();
        });

    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return content.minWidth(height);
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return content.maxWidth(height);
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return content.minHeight(width);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return content.maxHeight(width);
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return content.prefHeight(width);
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return content.prefWidth(height);
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {

        double middleY = contentHeight / 2;

        line.setStartX(0);
        line.setEndX(contentWidth);
        line.setStartY(middleY);
        line.setEndY(middleY);

        content.resizeRelocate(0, 0, contentWidth, contentHeight);
    }
}
