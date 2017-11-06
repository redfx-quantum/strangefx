package com.gluonhq.strange.ui;

import javafx.beans.Observable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Line;

import java.util.stream.Collectors;

public class CircuitSkin extends SkinBase<Circuit> {

    private Line line = new Line();
    private HBox hbox = new HBox();
    private Label label = new Label("IO>");

    private HBox gateRow = new HBox();
    private Circuit circuit;

    CircuitSkin(Circuit control) {
        super(control);
        this.circuit = control;
        getChildren().addAll(line, hbox);

        line.getStyleClass().add("center-line");
        label.getStyleClass().add("line-label");
        gateRow.getStyleClass().add("draggable-area");

        hbox.setStyle("-fx-padding: 10 5 10 0");

        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getChildren().addAll(label, gateRow, getSkinnable().getOutput());
        HBox.setHgrow(label, Priority.NEVER);
        HBox.setHgrow(gateRow, Priority.ALWAYS);

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
//            e.getSource();
//            System.out.println("drag dropped: "+e.getSource());

            Dragboard db = e.getDragboard();
            if ( db.hasContent(GateSymbol.DRAGGABLE_GATE) ) {

                // retriave symbol from global storage (only way to keep ref to the node)
                GateSymbol symbol = (GateSymbol) System.getProperties().get(GateSymbol.DRAGGABLE_GATE);
                if ( TransferMode.MOVE == e.getTransferMode()) {

                    // move the gate symbol between circuits
                    ((Pane)symbol.getParent()).getChildren().remove(symbol);
                    gateRow.getChildren().add(symbol);
                    e.setDropCompleted(true);

                } else {

                    // re-create gate symbol which was dragged from the toolbar
                    gateRow.getChildren().add(GateSymbol.of(symbol.getGate()));
                    e.setDropCompleted(true);
                }

                // clear our the ref to the dragged node
                System.getProperties().remove(GateSymbol.DRAGGABLE_GATE);
            }
            e.consume();
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
