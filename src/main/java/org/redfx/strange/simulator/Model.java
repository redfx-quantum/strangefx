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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.redfx.strange.simulator;

import org.redfx.strange.Gate;
import org.redfx.strange.gate.Identity;
import java.util.ArrayList;
import java.util.LinkedList;

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
    
    private int nqubits;
    
    private double[] beginState;

    private ObservableList<Double> endStates = FXCollections.observableArrayList();
    private BooleanProperty refreshRequest = new SimpleBooleanProperty();

    private static Model instance = new Model();
    
    private List<List<Gate>> gates = new LinkedList<>();
    
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
        this.gates = new LinkedList();
        for (int i = 0; i < n; i++) {
            this.gates.add(new LinkedList<Gate>());
        }
    }
    
    public int getNQubits() {
        return this.nqubits;
    }
    
    public List<List<Gate>> getGates() {
        return gates;
    }

    public void setGatesForCircuit(int n, List<Gate> gates) {
        /*
        for (Gate gate: gates) {
            gate.setMainQubit(n);
        }
        List<Gate> old = this.gates.get(n);
        boolean similar = true;
        if (gates.size() == old.size()) {
            for (int i = 0; i < gates.size(); i++) {
                if (!gates.get(i).equals(old.get(i))) {
                    similar = false; i = gates.size();
                }
            }
        } else {
            similar = false;
        }
        if (!similar) {
            this.gates.set(n, gates);
            refreshRequest.set(true);
        }
        */
    }

    public int getNumberOfSteps() {
        return this.gates.get(0).size();
    }
    
    public List<Gate> getStepsByCircuit(int idx) {
        return this.gates.get(idx);
    }
    
    public Gate[] getGatesByStep(int idx) {
        int nq = this.gates.size();
        Gate[] answer = new Gate[nq];
        for (int i = 0; i < nq; i++) {
            // if this gate didn't have a step, we'll add an I gate to it.
            if (this.gates.get(i).size() < (idx+1)) {
                List<Gate> old = this.gates.get(i);
                ArrayList<Gate> newList = new ArrayList<>();
                newList.addAll(old);
                newList.add(new Identity(i));
                this.gates.set(i, newList);
            }
            answer[i] = this.gates.get(i).get(idx);
        }
        return answer;
    }
    
    public String getGateDescription() {
        StringBuffer answer = new StringBuffer("[");
        int nq = this.getNQubits();
        for (int i = 0; i < getNumberOfSteps(); i++) {
            answer.append("[");
            for (int j = 0; j < nq;j++) {
                List<Gate> row = getGates().get(j);
                Gate target = new Identity(j);
                if (row.size() > i ) {
                    target = row.get(i);
                }
                answer.append(target.getName());
                if (j < nq-1) answer.append(",");
            }
            answer.append("]");
        }
        answer.append("]");
        return answer.toString();
    }
    
    public void printGates() {
        for (int i = 0; i < getNumberOfSteps(); i++) {
            System.out.println("step "+i+": "+getGatesByStep(i));
        }
    }
    
}
