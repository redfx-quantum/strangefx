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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.redfx.strangefx.simulator;

import org.redfx.strange.Gate;
import org.redfx.strange.gate.Identity;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.redfx.strange.Program;
import org.redfx.strange.Qubit;
import org.redfx.strange.Step;
import org.redfx.strange.gate.Cnot;
import org.redfx.strange.gate.X;
import org.redfx.strangefx.ui.PartialGate;

/**
 *
 * @author johan
 */
public class RenderModel {
    
    private int nqubits;
    
    private double[] beginState;

    private ObservableList<Double> endStates = FXCollections.observableArrayList();
   // private List<ObservableList<Double>> intermediateStates = new ArrayList<>();
    private Map<Integer, Qubit[]> intermediateStates = new HashMap<>();
    private BooleanProperty refreshRequest = new SimpleBooleanProperty();
    
    private ObjectProperty<ArrayList<Step>> stepsProperty = new SimpleObjectProperty<>();

    private static RenderModel instance = new RenderModel();
    
//    private List<List<Gate>> gates = new LinkedList<>();
    private ArrayList<Step> steps = new ArrayList<>();
    
    /**
     * Create a new, empty Model
     */
    public RenderModel() {        
    }
    
    /**
     * Create a Model based on an existing Program
     */
    public RenderModel(Program p) {
        this.nqubits = p.getNumberQubits();
        this.steps = new ArrayList(p.getNumberQubits());
        this.steps.addAll(p.getSteps());
    }
    
    @Deprecated
    public static RenderModel getInstance() {
        return instance;
    }
    
    public BooleanProperty refreshRequest() {
        return refreshRequest;
    }
    
    public ObservableList<Double> getEndStates() {
        return endStates;
    }
    
    public void setIntermediateProbabilities(Map<Integer, Qubit[]> intqubits) {
        this.intermediateStates = intqubits;
    }
    
    public Qubit[] getIntermediaStates(int idx) {
        return intermediateStates.get(idx);
    }
    
    public Map<Integer, Qubit> getIntermediateStatesByQubit(int qubitIndex) {
        Map<Integer, Qubit> answer = new HashMap<>();
        for (Integer idx : intermediateStates.keySet()) {
            Qubit[] row = intermediateStates.get(idx);
            answer.put(idx, row[qubitIndex]);
        }
        return answer;
    }
    
    /**
     * Set the number of qubits in this Model. This erases all previous information
     * @param n 
     */
    public void setNQubits(int n) {
        this.nqubits = n;
        this.beginState = new double[n];
        this.steps = new ArrayList(n);
    }
    
    public int getNQubits() {
        return this.nqubits;
    }

    
    public ArrayList<Step> getSteps() {
        return steps;
    }
    
    public ObjectProperty<ArrayList<Step>> stepsProperty() {
        return stepsProperty;
    }

    public int getNumberOfSteps() {
        return this.steps.size();
    }
  
    /**
     * Update the gates for the qubit at the specified index. The provided
     * <code>gateList</code> should contain a gate for every step (no null
     * values allowed). This function will check if partial gates match with
     * other gates in the same step, and if so, replace them (e.g. NOT and X 
     * CNOT).
     *
     * @param idx
     * @param gateList
     */
    public void updateGatesForQubit(int idx, ArrayList<Gate> gateList) {
        int length = gateList.size();
        for (int i = 0; i < length; i++) {
            Gate g = gateList.get(i);
            if (g != null) {
                while (steps.size() < i+1) {
                    steps.add(new Step(new Identity(idx)));
                }
                Step step = steps.get(i);
                List<Gate> exists = step.getGates();
                Gate removeMe = null;
                for (Gate exist : exists) {
                    if (exist.getMainQubitIndex() == idx) {
                        removeMe = exist;
                    }
                }
                if (removeMe != null) {
                    step.removeGate(removeMe);
                }
                step.addGate(g);
                // now that the gates in this step are complete, check if we can
                // replace a controlGate + something
                List<Gate> allGates = step.getGates();
                Optional<Gate> ctrlGate = allGates.stream().filter(c -> c.getGroup().equals(PartialGate.GROUP_PARTIAL)).findFirst();
                if (ctrlGate.isPresent()) {
                    Optional<Gate> xGate = allGates.stream().filter(c -> c.getClass().equals(X.class)).findFirst();
                    if (xGate.isPresent()) {
                        int cidx = ctrlGate.get().getMainQubitIndex();
                        int xidx = xGate.get().getMainQubitIndex();
                        System.err.println("CNOT!!");
                        Gate cnotGate = new Cnot(cidx, xidx);
                        Gate iGate = new Identity(xidx);
                        step.removeGate(ctrlGate.get());
                        step.removeGate(xGate.get());
                        step.addGate(cnotGate);
                       
                    }
                }
            }
        }
        
    }
    
}
