/*-
 * #%L
 * StrangeFX
 * %%
 * Copyright (C) 2020 Johan Vos
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
package org.redfx.strange.ui;

import java.util.ArrayList;
import org.redfx.strange.gate.*;
import org.redfx.strange.simulator.Model;
import org.redfx.strange.Gate;

import javafx.beans.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.shape.*;

import javafx.geometry.Insets;

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

    private Line line = new Line();
    private Line measuredLine = new Line();

    private Label title = new Label();
    private Measurement measurement = new Measurement();

    private Pane gateRow = new Pane();
    private HBox allGates = new HBox();
    private int idx; // the number of the qubit
  //  private ObservableList<GateSymbol> gates = FXCollections.observableArrayList();
    private Region oldParent = null;
    private ArrayList<GateSymbol> gateList = new ArrayList<>();
    private final Model model = Model.getInstance();

    private InvalidationListener endStateListener = (Observable o) -> {
        double mv = model.getEndStates().get(idx);
        measurement.setMeasuredChance(mv);
    };

    public QubitFlow(int index) {
        this.idx = index;
        title.setText(String.format("q[%d] I0>", idx));
        gateRow.getChildren().add(SPACER);
        getStyleClass().add("qubit");

        gateRow.getStyleClass().add("gate-row");
        title.getStyleClass().add("title");
        title.setPrefWidth(85);

        line.endXProperty().bind(widthProperty());
        line.getStyleClass().add("wire");
        measuredLine.endXProperty().bind(widthProperty());
        measuredLine.getStyleClass().add("wire");
        measuredLine.setVisible(false);

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

        measuredLine.setTranslateY(10);
        StackPane stack = new StackPane(line, measuredLine, base);
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

        //ensure all updates from the skin go back to control
//        gateRow.getChildren().addListener((Observable observable) -> {
//            getGateSymbols().setAll(
//                    gateRow.getChildren()
//                            .stream()
//                            .filter(g -> g != SPACER)
//                            .filter(g -> g instanceof GateSymbol)
//                            .map(n -> (GateSymbol) n).collect(Collectors.toList())
//            );
//        });

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
                    symbol = GateSymbol.of(symbol.getGate());
                    symbol.setTranslateX(spacerIndex * STEP_WIDTH);
                    gateRow.getChildren().add(symbol);
                                        insert(symbol,spacerIndex);

                    e.setDropCompleted(true);
                }
            }
            redraw();
            e.consume();
        });

        gateRow.setOnDragExited(e -> removeSpacer());
        model.getEndStates().addListener(endStateListener);
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
        GateSymbol symbol = GateSymbol.of(gate);;
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
        if (gate instanceof org.redfx.strange.gate.Measurement) {
            measuredLine.translateXProperty().bind(symbol.layoutXProperty().add(allGates.layoutXProperty()));
            measuredLine.setVisible(true);
        }
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
//
//    public ObservableList<GateSymbol> getGateSymbols() {
//        return this.gates;
//    }

    public void clear() {
        model.getEndStates().removeListener(endStateListener);
        gateRow.getChildren().clear();
    }

    public Measurement getOutput() {
        return measurement;
    }
    
    private void insert(GateSymbol g, int idx) {
        while (gateList.size() < idx ) gateList.add(gateList.size(), null);
        // now gateList.size is at least idx. Are we adding at the end?
        if (gateList.size() == idx) {
            gateList.add(gateList.size(), g);
            return;
        }
        // we are adding before the end of this row. If we have a null value on
        // the target place, we replave it.
        if (gateList.get(idx) == null) {
            gateList.set(idx, g);
            return;
        }
        // the desired element has an element already we need to right-shift all 
        gateList.add(idx, g);
    }
    
    /*
     * use the <code>gateList</code> list to recreate the gateRow
    */
    private void redraw() {
        gateRow.getChildren().clear();
        for (int i = 0; i < gateList.size(); i++) {
            if (gateList.get(i) != null) {
                double tx = i * STEP_WIDTH;
                GateSymbol s = (GateSymbol)(gateList.get(i));
                s.setTranslateX(tx);
                gateRow.getChildren().add(s);
            }
        }
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
