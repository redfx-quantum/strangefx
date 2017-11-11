package com.gluonhq.strange.ui;

import com.gluonhq.strange.Model;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;

import java.util.stream.Collectors;

public class Qubit extends Region {

    private Line line = new Line();

    private Label title = new Label();
    private Measurement measurement = new Measurement();
    private HBox gateRow = new HBox();


    private int idx; // the number of the qubit
    private ObservableList<GateSymbol> gates = FXCollections.observableArrayList();

    private final Model model = Model.getInstance();

    public Qubit( int index ) {

        this.idx = index;

        title.setText( String.format("q[%d] I0>", idx) );

        getStyleClass().add("qubit");

        gateRow.getStyleClass().add("gate-row");
        title.getStyleClass().add("title");

        line.endXProperty().bind(widthProperty());
        line.getStyleClass().add("wire");

        BorderPane base = new BorderPane();
        base.getStyleClass().add("base");
        base.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        base.setLeft(title);
        base.setCenter(gateRow);
        base.setRight(measurement);

        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setAlignment(measurement, Pos.CENTER);

        StackPane stack = new StackPane(line, base);

        stack.prefWidthProperty().bind(widthProperty());
        stack.prefHeightProperty().bind(heightProperty());

        getChildren().add(stack);


        // initial update from control's gates
        gateRow.getChildren().setAll( getGateSymbols());

        //ensure all updates from the skin go back to control
        gateRow.getChildren().addListener( (Observable observable) -> {
            getGateSymbols().setAll(
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

        model.getEndStates().addListener((Observable o)-> {
            double mv = model.getEndStates().get(idx);
            measurement.setMeasuredChance(mv);
        });

        gates.addListener( (Observable o) -> {
            model.setGatesForCircuit(
                    idx, gates.stream().map(GateSymbol::getGate).collect(Collectors.toList()));

//            output.setMeasuredChance(Math.random());
        });


    }

    public int getIndex() {
        return this.idx;
    }

    public ObservableList<GateSymbol> getGateSymbols() {
        return this.gates;
    }

    public Measurement getOutput() {
        return measurement;
    }
}
