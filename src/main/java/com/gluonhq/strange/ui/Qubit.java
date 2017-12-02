/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2017, Gluon Software
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.strange.ui;

import com.gluonhq.strange.Model;
import com.gluonhq.strange.simulator.Gate;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.PickResult;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;

import java.util.stream.Collectors;

public class Qubit extends Region {

    private  static GateSymbol SPACER = new GateSymbol( Gate.IDENTITY, false ) {{
        getStyleClass().setAll("gate-spacer");
        int SPACER_WIDTH = 5;
        setMaxWidth(SPACER_WIDTH);
        setMinWidth(SPACER_WIDTH);
        setText(null);
    }};

    private Line line = new Line();

    private Label title = new Label();
    private Measurement measurement = new Measurement();
    private HBox gateRow = new HBox();

    private int idx; // the number of the qubit
    private ObservableList<GateSymbol> gates = FXCollections.observableArrayList();

    private final Model model = Model.getInstance();

    private InvalidationListener endStateListener = (Observable o)-> {
        double mv = model.getEndStates().get(idx);
        measurement.setMeasuredChance(mv);
    };

    public Qubit( int index ) {

        this.idx = index;
        System.out.println("QUBIT with index "+index+" created");
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
                    gateRow.getChildren()
                           .stream()
                           .filter(g -> g != SPACER)
                           .map( n -> (GateSymbol)n).collect(Collectors.toList())
            );
        });

        gateRow.setOnDragOver(event -> {

            if (event.getGestureSource() != this &&
                // only accept gates
                event.getDragboard().getContent(GateSymbol.DRAGGABLE_GATE) != null) {

                // allow both copy(i.e. creation from toolbar) and move(between circuits)
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);


                if ( gateRow.getChildren().isEmpty() ) {

                    gateRow.getChildren().add(SPACER);

                } else {

                    PickResult pickResult = event.getPickResult();
                    Node intersectedNode = pickResult.getIntersectedNode();
                    double x = pickResult.getIntersectedPoint().getX();

                    if (intersectedNode instanceof GateSymbol && intersectedNode != SPACER) {
                        removeSpacer();
                        int nodeIndex = gateRow.getChildren().indexOf(intersectedNode);
                        int insertionIndex = (x <= ((GateSymbol) intersectedNode).getWidth() / 2) ? nodeIndex : nodeIndex + 1;
                        gateRow.getChildren().add(insertionIndex, SPACER);

                    } else if ( intersectedNode == gateRow &&
                                x >= getOccupiedWidth() &&
                                gateRow.getChildren().indexOf(SPACER) < gateRow.getChildren().size() ) {
                            removeSpacer();
                            gateRow.getChildren().add(SPACER);
                    }
                }

            }

            event.consume();

        });

        gateRow.setOnDragDropped(e -> {

            Dragboard db = e.getDragboard();
            if ( db.hasContent(GateSymbol.DRAGGABLE_GATE) ) {

                // retrieve symbol from global storage (only way to keep ref to the node)
                GateSymbol symbol = (GateSymbol) System.getProperties().get(GateSymbol.DRAGGABLE_GATE);
                if ( TransferMode.MOVE == e.getTransferMode()) {
                    // move the gate symbol between circuits
                    symbol.removeFromParent();
                    int spacerIndex = gateRow.getChildren().indexOf(SPACER);
                    gateRow.getChildren().set(spacerIndex,symbol);
                    e.setDropCompleted(true);
                } else {
                    // re-create gate symbol which was dragged from the toolbar
                    symbol = GateSymbol.of(symbol.getGate());
                    int spacerIndex = gateRow.getChildren().indexOf(SPACER);
                    gateRow.getChildren().set(spacerIndex,symbol);
                    e.setDropCompleted(true);
                }
            }

            e.consume();
        });

        gateRow.setOnDragExited( e -> removeSpacer());

        model.getEndStates().addListener(endStateListener);

        gates.addListener( (Observable o) -> {
            model.setGatesForCircuit(
                    idx, gates.stream().map(GateSymbol::getGate).collect(Collectors.toList()));
        });

    }

    private void removeSpacer() {
        gateRow.getChildren().remove(SPACER);
    }

    private double getOccupiedWidth() {

        double width = 0;
        for ( Node node: gateRow.getChildren()) {
            width += ((GateSymbol)node).getWidth();
        }
        return width + gateRow.getSpacing() * gateRow.getChildren().size();

    }

    public int getIndex() {
        return this.idx;
    }

    public ObservableList<GateSymbol> getGateSymbols() {
        return this.gates;
    }

    public void clear() {
        model.getEndStates().removeListener(endStateListener);
        gateRow.getChildren().clear();
    }

    public Measurement getOutput() {
        return measurement;
    }
}
