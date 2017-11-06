/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gluonhq.strange;

import com.gluonhq.strange.simulator.Gate;
import com.gluonhq.strange.simulator.GateConfig;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author johan
 */
public class Model {
    
    public static final int GATE_NOT = 1;
    public static final int GATE_HADAMARD = 2;
    
    private int nqubits;
    
    private double[] beginState;
    private GateConfig gates = GateConfig.of(Gate.NOGATE);

    private ObservableList<Double> endStates = FXCollections.observableArrayList();
    
    private static Model instance = new Model();
    
    
    private Model() {        
    }
    
    public static Model getInstance() {
        return instance;
    }
    
    public ObservableList<Double> getEndStates() {
        return endStates;
    }
    
    public void setNQubits(int n) {
        this.nqubits = n;
        this.beginState = new double[n];
    }
    
    public int getNQubits() {
        return this.nqubits;
    }
    
    
    public void setGates(GateConfig gates) {
        this.gates = gates;
    }
    
    public void setGatesForQubit(int n, List<Gate> gates) {
        this.gates.set(0, gates);
    }
    
    public int getNumberOfSteps() {
        return this.gates.size();
    }
    
    public List<Gate> getStep(int i) {
        return this.gates.get(i);
    }
}
