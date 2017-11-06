/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gluonhq.strange;

import com.gluonhq.strange.simulator.Gate;
import com.gluonhq.strange.simulator.GateConfig;
import java.util.ArrayList;

import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    private GateConfig gates = GateConfig.initial(0);

    private ObservableList<Double> endStates = FXCollections.observableArrayList();
    private BooleanProperty refreshRequest = new SimpleBooleanProperty();

    private static Model instance = new Model();
    
    private Model() {        
    }
    
    public static Model getInstance() {
        return instance;
    }
    
    public BooleanProperty refreshRequest() {
        return refreshRequest;
    }
    
    public ObservableList<Double> getEndStates() {
        return endStates;
    }
    
    public void setNQubits(int n) {
        this.nqubits = n;
        this.beginState = new double[n];
        this.gates = GateConfig.initial(n);
    }
    
    public int getNQubits() {
        return this.nqubits;
    }
    
    
    public void setGates(GateConfig gates) {
        this.gates = gates;
    }
    
    public void setGatesForCircuit(int n, List<Gate> gates) {
        System.out.println("BEFORE, size = "+this.gates.get(n).size());
        System.out.println("gates was "+this.gates.get(n)+" and this = "+this);
        this.gates.set(n, gates);
        System.out.println("AFTER, size = "+this.gates.get(n).size());
        refreshRequest.set(true);
    }
    
    public int getNumberOfSteps() {
        return this.gates.get(0).size();
    }
    
    public List<Gate> getStepsByCircuit(int idx) {
        return this.gates.get(idx);
    }
    
    public List<Gate> getGatesByStep(int idx) {
        int nq = this.gates.size();
        ArrayList<Gate> answer = new ArrayList<>();
        for (int i = 0; i < nq; i++) {
            // if this gate didn't have a step, we'll add an I gate to it.
            if (this.gates.get(i).size() < (idx+1)) {
                System.out.println("Adding an I gate to circuit "+i);
                List<Gate> old = this.gates.get(i);
                ArrayList<Gate> newList = new ArrayList<>();
                newList.addAll(old);
                newList.add(Gate.IDENTITY);
                this.gates.set(i, newList);
            }
            answer.add(this.gates.get(i).get(idx));
        }
        return answer;
    }
}
