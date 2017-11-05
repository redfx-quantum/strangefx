/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gluonhq.strange;

import com.gluonhq.strange.ui.Gate;
import java.util.List;

/**
 *
 * @author johan
 */
public class Model {
    
    public static final int GATE_NOT = 1;
    public static final int GATE_HADAMARD = 2;
    
    private int nqubits;
    
    private double[] beginState;
    private int[][] gates = new int[0][0];
    
    private static Model instance = new Model();
    
    private Model() {        
    }
    
    public static Model getInstance() {
        return instance;
    }
    
    public void setNQubits(int n) {
        this.nqubits = n;
        this.beginState = new double[n];
    }
    
    public int getNQubits() {
        return this.nqubits;
    }
    
    
    public void setGates(int[][] g) {
        this.gates = g;
    }
    
    public void setGatesForQubit(int n, List<Gate> gates) {
        this.gates[n] = new int[gates.size()];
        for (int i = 0; i < gates.size(); i++) {
            this.gates[n][i] = gates.get(i).getType();
        }
    }
    
    public int getNumberOfSteps() {
        return this.gates.length;
    }
    
    public int[] getStep(int i) {
        return this.gates[i];
    }
}
