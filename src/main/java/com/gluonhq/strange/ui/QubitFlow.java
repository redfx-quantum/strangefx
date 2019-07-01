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

import com.gluonhq.strange.gate.*;
import com.gluonhq.strange.simulator.Model;
import com.gluonhq.strange.Gate;
import com.gluonhq.strange.simulator.local.LocalSimulator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.PickResult;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

import java.util.stream.Collectors;

public class QubitFlow extends Region {

    private boolean askOnTop = false;
    private static GateSymbol SPACER = new GateSymbol(new Identity(0), false) {{
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
    private HBox allGates = new HBox();
    private int idx; // the number of the qubit
    private ObservableList<GateSymbol> gates = FXCollections.observableArrayList();
    private Region oldParent = null;
    private final Model model = Model.getInstance();

    private InvalidationListener endStateListener = (Observable o) -> {
        double mv = model.getEndStates().get(idx);
        measurement.setMeasuredChance(mv);
    };

    public QubitFlow(int index) {

        this.idx = index;
        System.out.println("QUBIT with index " + index + " created");
        title.setText(String.format("q[%d] I0>", idx));
        gateRow.getChildren().add(SPACER);
        getStyleClass().add("qubit");

        gateRow.getStyleClass().add("gate-row");
        title.getStyleClass().add("title");

        line.endXProperty().bind(widthProperty());
        line.getStyleClass().add("wire");

        BorderPane base = new BorderPane();
        base.getStyleClass().add("base");
        base.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        base.setLeft(title);
        base.setCenter(allGates);
        HBox transparent = new HBox();
        transparent.setOpacity(0.01);
        transparent.setPrefWidth(1024);
        allGates.getChildren().addAll(gateRow, transparent);
        HBox.setHgrow(transparent, Priority.ALWAYS);
        base.setRight(measurement);

        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setAlignment(measurement, Pos.CENTER);

        StackPane stack = new StackPane(line, base);
        this.sceneProperty().addListener(
                new InvalidationListener() {
                    @Override
                    public void invalidated(Observable observable) {
                        if (QubitFlow.this.getScene() != null) {
                            QubitFlow.this.prefWidthProperty().bind(QubitFlow.this.getScene().widthProperty());
                        }
                    }
                }
        );

        stack.prefWidthProperty().bind(widthProperty());
        stack.prefHeightProperty().bind(heightProperty());

        getChildren().add(stack);


        // initial update from control's gates
        gateRow.getChildren().setAll(getGateSymbols());

        //ensure all updates from the skin go back to control
        gateRow.getChildren().addListener((Observable observable) -> {
            getGateSymbols().setAll(
                    gateRow.getChildren()
                            .stream()
                            .filter(g -> g != SPACER)
                            .filter(g -> g instanceof GateSymbol)
                            .map(n -> (GateSymbol) n).collect(Collectors.toList())
            );
        });

        gateRow.setOnDragOver(event -> {

            if (event.getGestureSource() != this &&
                    // only accept gates
                    event.getDragboard().getContent(GateSymbol.DRAGGABLE_GATE) != null) {

                // allow both copy(i.e. creation from toolbar) and move(between circuits)
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);


                if (gateRow.getChildren().isEmpty()) {

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

                    } else if (intersectedNode == gateRow &&
                            x >= getOccupiedWidth() &&
                            gateRow.getChildren().indexOf(SPACER) < gateRow.getChildren().size()) {
                        removeSpacer();
                        gateRow.getChildren().add(SPACER);
                    }
                }

            }

            event.consume();

        });

        gateRow.setOnDragDropped(e -> {

            Dragboard db = e.getDragboard();
            if (db.hasContent(GateSymbol.DRAGGABLE_GATE)) {

                // retrieve symbol from global storage (only way to keep ref to the node)
                GateSymbol symbol = (GateSymbol) System.getProperties().get(GateSymbol.DRAGGABLE_GATE);
                if (TransferMode.MOVE == e.getTransferMode()) {
                    // move the gate symbol between circuits
                    symbol.removeFromParent();
                    int spacerIndex = gateRow.getChildren().indexOf(SPACER);
                    gateRow.getChildren().set(spacerIndex, symbol);
                    e.setDropCompleted(true);
                } else {
                    // re-create gate symbol which was dragged from the toolbar
                    symbol = GateSymbol.of(symbol.getGate());
                    int spacerIndex = gateRow.getChildren().indexOf(SPACER);
                    gateRow.getChildren().set(spacerIndex, symbol);
                    e.setDropCompleted(true);
                }
            }

            e.consume();
        });

        gateRow.setOnDragExited(e -> removeSpacer());

        model.getEndStates().addListener(endStateListener);

        gates.addListener((Observable o) -> {
            model.setGatesForCircuit(
                    idx, gates.stream().map(gs -> createGate(gs.getGate())).collect(Collectors.toList()));
        });

    }

    private void removeSpacer() {
        gateRow.getChildren().remove(SPACER);
    }

    public boolean wantsOnTop() {
        return askOnTop;
    }
    public GateSymbol addGate(Gate gate) {
        if (gateRow.getChildren().isEmpty()) {
            gateRow.getChildren().add(SPACER);
        }
        GateSymbol symbol = GateSymbol.of(gate);
        if (gate instanceof Oracle) {
            this.askOnTop = true;
//            // we need to span more wires
//            Oracle oracle = (Oracle)gate;
//            int span = oracle.getQubits();
//            Rectangle r = new Rectangle(0,0,40,160);
//            r.setFill(Color.GREEN);
//            symbol = new Group();
//            Group group = (Group)symbol;
//            group.toFront();
//            r.toFront();
//            symbol.setManaged(false);
//            symbol.toFront();
//            group.setTranslateX(50 * gateRow.getChildren().size());
//            group.getChildren().add(r);
        }
      //  GateSymbol symbol = GateSymbol.of(gate);
        int spacerIndex = gateRow.getChildren().indexOf(SPACER);
        if (spacerIndex < 0) {
            gateRow.getChildren().add(symbol);
        } else {
            gateRow.getChildren().set(spacerIndex, symbol);
        }
        return symbol;
    }

    /**
     * Add the additional symbol for the gate
     * @param gate
     * @param idx the index of the additional qubit (0 = main index, 1 = first additional index)
     * @return
     */
    public GateSymbol addAddtionalGateSymbol(Gate gate, int idx) {
        if (gateRow.getChildren().isEmpty()) {
            gateRow.getChildren().add(SPACER);
        }
        GateSymbol symbol = GateSymbol.of(gate, idx);
        int spacerIndex = gateRow.getChildren().indexOf(SPACER);
        if (spacerIndex < 0) {
            gateRow.getChildren().add(symbol);
        } else {
            gateRow.getChildren().set(spacerIndex, symbol);
        }
        return symbol;
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

    private Gate createGate(Gate g) {
        return g;
//        Class<? extends Gate> gateClass = g.getClass();
//        try {
//            Constructor<? extends Gate> constructor = gateClass.getConstructor(int.class);
//            Gate copyGate = constructor.newInstance(g.getAffectedQubitIndex().get(0));
//            return copyGate;
//
//        } catch (Exception ex) {
//            Logger.getLogger(LocalSimulator.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
    }

}
