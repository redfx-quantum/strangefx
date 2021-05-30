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
package org.redfx.strangefx.ui;

import org.redfx.strangefx.simulator.RenderModel;
import org.redfx.strange.ui.render.*;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.*;
import javafx.scene.layout.*;

import java.util.*;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.scene.shape.Line;
import org.redfx.strange.Gate;
import org.redfx.strange.Program;
import org.redfx.strange.QuantumExecutionEnvironment;
import org.redfx.strange.Qubit;
import org.redfx.strange.Result;
import org.redfx.strange.Step;
import org.redfx.strange.local.SimpleQuantumExecutionEnvironment;

public class QubitBoard extends Group {
    
    public static final int WIRE_HEIGHT = 77; // the vertical distance between 2 wires
    private final RenderModel model;
    private ObservableList<QubitFlow> wires = FXCollections.observableArrayList();

    private final int nQubits;
    private Line[] line;

    public QubitBoard(RenderModel model) {
        this.model = model;
        this.nQubits = model.getNQubits();
        line = new Line[nQubits];
        for (int i = 0; i < nQubits; i++) {
            line[i] = new Line();
            line[i].setTranslateY(WIRE_HEIGHT * i + WIRE_HEIGHT / 2);
            line[i].getStyleClass().add("wire");
            getChildren().add(line[i]);
        }

        wires.addListener((Observable o) -> {
            model.refreshRequest().set(true);
        });

        model.stepsProperty().addListener((Observable observable) -> {
            processCircuit(model.stepsProperty().get());
            renderCircuit();
        });

        for (int i = 0; i < nQubits; i++) {
            appendQubit();
            QubitFlow q = wires.get(i);
            q.setTranslateY(i * GateSymbol.SEP);
            getChildren().add(q);
            line[i].endXProperty().bind(q.widthProperty());

        }
    }


    public void addOverlay(BoardOverlay overlay) {
        this.getChildren().add(overlay);
    }

    public ObservableList<QubitFlow> getWires() {
        return wires;
    }

    public void appendQubit() {
        QubitFlow flow = new QubitFlow(wires.size(), model);
        wires.add(flow);
    }

    public void clear() {
        wires.forEach(QubitFlow::clear);
        wires.removeIf(qb -> qb.getIndex() > (nQubits - 1));
    }
    
    private void renderCircuit() {
        clear();
        for (Step step: model.getSteps()) {
            List<Gate> gates = step.getGates();
            boolean[] gotit = new boolean[nQubits];
            for (Gate gate : gates) {
                int qb = gate.getMainQubitIndex();
                System.err.println("qb = "+qb);
                gotit[qb] = true;
                QubitFlow wire = wires.get(qb);
                wire.setMinWidth(480);
                System.err.println("Calling addGate");
                GateSymbol symbol = wire.addGate(gate);
            }
        }
    }
    
    public List<QubitFlow> getQubitFlows() {
        return this.wires;
    }
    
    private void processCircuit(ArrayList<Step> steps) {
        System.err.println("Process circuit with "+wires.size()+" qubits and "+steps.size()+" steps.");
        Program p = new Program(wires.size());
        for (Step step : steps) {
            System.err.println("Step: "+step);
            p.addStep(step);
        }
        QuantumExecutionEnvironment qee = new SimpleQuantumExecutionEnvironment();
        Consumer<Result> resultConsumer = (t) -> {
            Platform.runLater(() -> {
                Qubit[] qubits = t.getQubits();
                ObservableList<Double> endStates = model.getEndStates();
                for (int i = 0; i < wires.size(); i++) {
                    if (endStates.size() > i) {
                        endStates.set(i, qubits[i].getProbability());
                    } else {
                        endStates.add(i, qubits[i].getProbability());
                    }
                }
            });
        };
        qee.runProgram(p, resultConsumer);
    }
    
    public void redraw() {
        wires.forEach(w -> w.redraw());
    }
}
