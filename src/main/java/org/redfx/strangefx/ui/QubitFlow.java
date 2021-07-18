/*-
 * #%L
 * StrangeFX
 * %%
 * Copyright (C) 2020, 2021 Johan Vos
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Johan Vos nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.redfx.strangefx.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.redfx.strange.gate.*;
import org.redfx.strangefx.simulator.RenderModel;
import org.redfx.strange.Gate;

import javafx.beans.*;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.shape.*;

import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import org.redfx.strange.Complex;
import org.redfx.strange.Program;
import org.redfx.strange.Qubit;
import org.redfx.strange.Result;
import org.redfx.strange.Step;

public class QubitFlow extends Region {

    private final int STEP_WIDTH = 44;
    private boolean askOnTop = false;
    private final static GateSymbol SPACER = new GateSymbol(new Identity(0), false) {
        {
            getStyleClass().setAll("gate-spacer");
            int SPACER_WIDTH = 5;
            setMaxWidth(SPACER_WIDTH);
            setMinWidth(SPACER_WIDTH);
            setText(null);
        }
    };

    private Label title = new Label();
    private MeasurementUI measurement = new MeasurementUI();
    private List<MeasurementUI> intermediates = new ArrayList<>();
    
    private Pane gateRow = new Pane();
    private HBox allGates = new HBox();
    private final int idx; // the number of the qubit

    private Region oldParent = null;
    
    // This list is the authoritive list for all gates in this wire.
    // the gateRow with allGates is rendered from this list.
    // elements are added to this list by drag&drop or when the board is 
    // created from an existing program.
    private final ArrayList<Gate> gateList = new ArrayList<>();
    private final RenderModel model;
    private InvalidationListener endStateListener;

    public QubitFlow(int index, RenderModel model) {
        this.model = model;
        this.idx = index;
        fillGatesFromModel(model);
        title.setText(String.format("q[%d] I0>", idx));
        gateRow.getChildren().add(SPACER);
        getStyleClass().add("qubit");
      //   this.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");

        gateRow.getStyleClass().add("gate-row");
        title.getStyleClass().add("title");
        title.setPrefWidth(85);


        BorderPane base = new BorderPane();
        base.getStyleClass().add("base");
        base.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        base.setLeft(title);
        base.setCenter(allGates);
        allGates.getChildren().addAll(gateRow);
        HBox.setHgrow(gateRow, Priority.ALWAYS);
        base.setRight(measurement);

        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setAlignment(measurement, Pos.CENTER);

        StackPane stack = new StackPane(base);
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

        this.setOnDragOver(event -> {

            if (event.getGestureSource() != this
                    && // only accept gates
                    event.getDragboard().getContent(GateSymbol.DRAGGABLE_GATE) != null) {

                // allow both copy(i.e. creation from toolbar) and move(between circuits)
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                int intidx = getInternalIndex(event.getX());
                removeSpacer();
                SPACER.setTranslateX(intidx * STEP_WIDTH);
                gateRow.getChildren().add(SPACER);
            }
            event.consume();
        });

        this.setOnDragDropped(e -> {
            double xzero = allGates.getLayoutX();
            double xtrans = e.getX() - xzero;
            Dragboard db = e.getDragboard();
            if (db.hasContent(GateSymbol.DRAGGABLE_GATE)) {
                int spacerIndex = getInternalIndex(e.getX());
                // retrieve symbol from global storage (only way to keep ref to the node)
                GateSymbol symbol = (GateSymbol) System.getProperties().get(GateSymbol.DRAGGABLE_GATE);
                GridPane.setMargin(symbol, new Insets(2, 0, 2, 0));
                removeSpacer();

                if (TransferMode.MOVE == e.getTransferMode()) {
                    // move the gate symbol between circuits
                    symbol.removeFromParent();
                    symbol.setTranslateX(spacerIndex * STEP_WIDTH);
                    insert(symbol,spacerIndex);
                    gateRow.getChildren().add(symbol);
                    e.setDropCompleted(true);
                } else {
                    // re-create gate symbol which was dragged from the toolbar
                    if (symbol.getGate() == null) {
                        symbol = GateSymbol.of(GateSymbol.ControlQubit.OFF);
                    } else {
                        symbol = GateSymbol.of(symbol.getGate().getClass(), idx);
                    }
                    symbol.setTranslateX(spacerIndex * STEP_WIDTH);
                    gateRow.getChildren().add(symbol);
                    insert(symbol,spacerIndex);
                    e.setDropCompleted(true);
                }
            }
            redraw();
            updateModel();
            e.consume();
        });

        gateRow.setOnDragExited(e -> removeSpacer());
        this.endStateListener = createEndStateListener();
        model.getEndStates().addListener(endStateListener);
        redraw();
    }

    private InvalidationListener createEndStateListener() {
        InvalidationListener answer = (Observable o) -> {
            if ((model != null) && (model.getEndStates().size() > idx)) {
                double mv = model.getEndStates().get(idx);
                measurement.setMeasuredChance(mv);
            }
        };
        return answer;
    }

    public void cleanup() {
        model.getEndStates().removeListener(endStateListener);
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
        }
        int spacerIndex = gateRow.getChildren().indexOf(SPACER);
        if (spacerIndex < 0) {
            gateRow.getChildren().add(symbol);
        } else {
            gateRow.getChildren().set(spacerIndex, symbol);
        }
        redraw();
        return symbol;
    }

    /**
     * Add the additional symbol for the gate
     *
     * @param gate
     * @param gateidx the index of the additional qubit (0 = main index, 1 =
     * first additional index)
     * @return
     */
    public GateSymbol addAdditonalGateSymbol(Gate gate, int gateidx) {
        if (gateRow.getChildren().isEmpty()) {
            gateRow.getChildren().add(SPACER);
        }
        GateSymbol symbol = GateSymbol.of(gate, gateidx);
        int spacerIndex = gateRow.getChildren().indexOf(SPACER);
        if (spacerIndex < 0) {
            gateRow.getChildren().add(symbol);
        } else {
            gateRow.getChildren().set(spacerIndex, symbol);
        }
        return symbol;
    }

    /**
     * For testing only!
     * @return 
     */
    public Pane getGateRow() {
        return this.gateRow;
    }
    
    private double getOccupiedWidth() {

        double width = 0;
        int cnt = 0;
        for (Node node : gateRow.getChildren()) {
            GateSymbol gs = (GateSymbol) node;
            if (!gs.isIdentity()) {
                width += ((GateSymbol) node).getWidth();
                cnt++;
            }
        }
        return width + STEP_WIDTH * cnt;

    }

    private int getInternalIndex(double x) {
        double dist = x - this.allGates.getLayoutX();
        double div = dist / STEP_WIDTH;
        return (int) (div);
    }

    public int getIndex() {
        return this.idx;
    }


    public void clear() {
        model.getEndStates().removeListener(endStateListener);
        gateRow.getChildren().clear();
    }

    public MeasurementUI getOutput() {
        return measurement;
    }
    
    /** 
     * Insert a gate with the given symbol at the index specified by locationIndex
     * Note that locationIndex is NOT the index of this qubit, which is represented
     * by idx
     * @param gs
     * @param locationIndex 
     */
    private void insert(GateSymbol gs, int locationIndex) {
        Gate g = gs.getGate();
        g.setMainQubitIndex(idx);
        while (gateList.size() < locationIndex ) gateList.add(gateList.size(), new Identity(idx));
        // now gateList.size is at least idx. Are we adding at the end?
        if (gateList.size() == locationIndex) {
            gateList.add(gateList.size(), g);
            return;
        }
        // we are adding before the end of this row. If we have an Identiy value on
        // the target place, we replave it.
        if (gateList.get(locationIndex) instanceof Identity) {
            gateList.set(locationIndex, g);
            return;
        }
        // the desired element has an element already we need to right-shift all 
        gateList.add(locationIndex, g);
    }
    
    /*
     * use the <code>gateList</code> list to recreate the gateRow
    */
    public void redraw() {
        gateRow.getChildren().clear();
        intermediates.clear();
        Map<Integer, Qubit> intermediateValues = model.getIntermediateStatesByQubit(idx);
        double deltax = 0;
        int iv = 0;
        for (Gate gate : gateList) {
            if (gate != null) {
                Region symbol = null;
                if (gate instanceof Measurement) {
                    symbol = createMeasurementLine(gate);
                } else if (gate instanceof ProbabilitiesGate) {
                    ProbabilitiesGate pg = (ProbabilitiesGate)gate;
                    symbol = pg.createUI();
                } else {
                    GateSymbol gs = GateSymbol.of(gate);
                    gs.setWire(this);
                    symbol = gs;
                }
                symbol.setTranslateX(deltax);
                symbol.translateYProperty().bind(gateRow.heightProperty().add(-1 * GateSymbol.HEIGHT).divide(2));
                gateRow.getChildren().add(symbol);
                BorderPane.setAlignment(symbol, Pos.CENTER);
            }
            iv++;
            deltax += STEP_WIDTH;
        }
    }
    
    public void gateSymbolRemoved(GateSymbol symbol) {
        Optional<Gate> target = gateList.stream().filter(g -> g.equals(symbol.getGate())).findFirst();
        if (target.isPresent()) {
            int stepidx = gateList.indexOf(target.get());
            gateList.remove(stepidx);
            gateList.add(stepidx, new Identity(idx));
        } else {
            System.err.println("Didn't find gate");
        }
        updateModel();
    }
    
    private void updateModel() {
        model.updateGatesForQubit(idx, gateList);
        model.refreshRequest().set(true);
    }
   
    private Gate createGate(Gate g) {
        return g;
    }

    /**
     * Given the provided model, add Gate instances to this qubit. If a Step has no Gate for
     * this qubit, add the Identity.
     * @param model 
     */
    private void fillGatesFromModel(RenderModel model) {
        gateList.clear();
        for (Step s : model.getSteps()) {
            if (s.getType() == Step.Type.PSEUDO) {
                this.gateList.add(new Measurement(this.idx));
            } else if (s.getType() == Step.Type.PROBABILITY) {
                if (this.idx ==0) {
                    this.gateList.add(new ProbabilitiesGate(s));
                } else {
                    this.gateList.add(new Identity(this.idx));
                }
            } else {
                Optional<Gate> hasGate = s.getGates().stream().filter(g -> g.getMainQubitIndex() == this.idx).findFirst();
                if (hasGate.isPresent()) {
                    this.gateList.add(hasGate.get());
                } else {
                    this.gateList.add(new Identity(this.idx));
                }
            }
        }
    }

    private Region createMeasurementLine(Gate gate) {
        GateSymbol gs = GateSymbol.of(gate);
        gs.setWire(this);
        Line l = new Line(STEP_WIDTH/2, STEP_WIDTH/2 + 8, 100, STEP_WIDTH/2 + 8);
        l.getStyleClass().add("wire");
        l.endXProperty().bind(widthProperty());
        Pane canvas = new Pane();
        canvas.getChildren().addAll(l, gs);
        return canvas;
    }

     private Group createProbability(Step s) {
        Program program = s.getProgram();
        Result result = program.getResult();
        Complex[] ip = result.getIntermediateProbability(s.getIndex());
        int nq = program.getNumberQubits();
        int N = 1 << nq;
        double deltaY = (66. * nq - 10 + 38) / N;
        Group answer = new Group();

        Rectangle rect2 = new Rectangle(0, 0, 40, 66 * nq - 10 + 38);
        rect2.setFill(Color.WHITE);
        rect2.setStroke(Color.BLUE);
        rect2.setStrokeWidth(1);
        answer.getChildren().add(rect2);

        for (int i = 0; i < N; i++) {
            double startY = i * deltaY;
            Rectangle minibar = new Rectangle(1, i * deltaY, 38 * ip[i].abssqr(), deltaY - 1);
            minibar.setFill(Color.GREEN);
            Line l = new Line(1, startY, 39, startY);
            l.setFill(Color.LIGHTGRAY);
            l.setStrokeWidth(1);
            answer.getChildren().add(l);
            answer.getChildren().add(minibar);
        }
        return answer;
    }

}
