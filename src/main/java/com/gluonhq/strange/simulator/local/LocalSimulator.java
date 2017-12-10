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
package com.gluonhq.strange.simulator.local;

import com.gluonhq.connect.ConnectState;
import com.gluonhq.connect.GluonObservableObject;
import com.gluonhq.strange.simulator.Gate;
import com.gluonhq.strange.simulator.GateConfig;
import com.gluonhq.strange.simulator.Simulator;
import com.gluonhq.strange.Model;
import com.gluonhq.strange.math.Complex;
import com.gluonhq.strange.simulator.CloudSimulator;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

/**
 *
 * @author johan
 */
public class LocalSimulator implements Simulator {

    private final int LOCAL_TRESHOLD = 1;
    private final Model model = Model.getInstance();
    CloudSimulator cloudSimulator = new CloudSimulator();

    public LocalSimulator() {
        model.refreshRequest().addListener((obs, oldv, newv) -> {
            System.out.println("REFRESH REQUEST changed to "+newv);
            if (newv) {
                double[] res2;
                if (model.getNQubits() < LOCAL_TRESHOLD) {
                    res2 = calculateQubitStates(model);
                    printResults2(res2);
                    List<Double> reslist = new ArrayList<>();
                    for (double d : res2) {
                        reslist.add(d);
                    }
                    model.getEndStates().setAll(reslist);
                    model.refreshRequest().set(false);
                } else {
                    System.out.println("CLOUDREQUEST! "+model.getNQubits());
                    GluonObservableObject<String> cloudres = cloudSimulator.calculateResults(model.getGateDescription());
                    cloudres.stateProperty().addListener(new InvalidationListener() {
                        @Override
                        public void invalidated(Observable o) {
                            System.out.println("NEW STATE in localsim = "+cloudres.getState());
                            
                            if (ConnectState.SUCCEEDED.equals(cloudres.getState())) {
                                String answer = cloudres.get();
                                System.out.println("got answer from cloud: " + answer);
                                String[] split = answer.split(";");
                                System.out.println("size = " + split.length);
                                final Complex[] probability = new Complex[split.length];
                                for (int i = 0; i < split.length; i++) {
                                    // TODO: allow complex numbers to be returned from cloud #23
                                    probability[i] = new Complex(Double.parseDouble(split[i]));
                                    System.out.println("prob["+i+"] = "+probability[i]);
                                }
                                double[] stateresult = calculateQubitStatesFromVector(probability);
                                List<Double> reslist = new ArrayList<>();
                                for (double d : stateresult) {
                                    reslist.add(d);
                                }
                                try {
                                    // only update the state if the size of the returned answer matches the #qubits in the model
                                    int nq  = model.getNQubits();
                                    if (nq == reslist.size()) {
System.out.println("will now set endstates with a vector size "+reslist.size());
                                        model.getEndStates().setAll(reslist);
                                    System.out.println("endstates = " + model.getEndStates());

                                    } else {
                                        System.out.println("ignore update, model has "+nq);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                model.refreshRequest().set(false);
                            }
                        }
                    });
                }

            }
        });
    }

    private void calculateResultsLocal(Model m) {

    }

    @Override
    public Complex[] calculateResults(Gate[][] gates) {
        int nQubits = gates.length;
        int nSteps = gates[0].length;
        Complex[] result = new Complex[1 << nQubits];
        result[0] = Complex.ONE;
        for (int i = 1; i < 1 << nQubits; i++) result[i] = Complex.ZERO;

        for (int i = 0; i < nSteps; i++) {
            Gate[] operations = getColumn(gates, i);
            //    System.out.println("--- apply step "+i+" with gates "+m.getGatesByStep(i));
            result = applyStep(operations, result, nQubits);
            System.out.println("--- applied step " + i + ", result = ");
            for (int j = 0; j < result.length; j++) {
                System.out.println("res[" + j + "] = " + result[j]);
            }
        }
        return result;
    }

    @Override
    public Complex[] calculateResults(Model m) {
        System.out.println("Calculate results for " + m.getNQubits() + " qubits and " + m.getNumberOfSteps() + " steps");
        Gate[][] gates = toMatrix(m.getGates());
        m.printGates();
        System.out.println("GATES = " + m.getGateDescription());
        int n = gates.length;
        int v = 1 << n;
        Complex[] result = new Complex[v];
        result[0] = Complex.ONE;
        for (int i = 1; i < v; i++) result[i] = Complex.ZERO;
        for (int i = 0; i < m.getNumberOfSteps(); i++) {
            //    System.out.println("--- apply step "+i+" with gates "+m.getGatesByStep(i));
            result = applyStep(m.getGatesByStep(i), result, n);
            System.out.println("--- applied step " + i + ", result = ");
            for (int j = 0; j < result.length; j++) {
                System.out.println("res[" + j + "] = " + result[j]);
            }
        }
        return result;
    }

    private Complex[][] tensor(Complex[][] a, Complex[][] b) {
        int d1 = a.length;
        int d2 = b.length;
        System.out.println("tensor product, d1 = " + d1 + " and d2 = " + d2);
        Complex[][] result = new Complex[d1 * d2][d1 * d2];
        for (int rowa = 0; rowa < d1; rowa++) {
            for (int cola = 0; cola < d1; cola++) {
                for (int rowb = 0; rowb < d2; rowb++) {
                    for (int colb = 0; colb < d2; colb++) {
                        result[d2 * rowa + rowb][d2 * cola + colb] = a[rowa][cola].mul( b[rowb][colb]);
                    }
                }
            }
        }
        return result;
    }

    private Gate[][] toMatrix(List<List<Gate>> gateList) {
        int nqubits = gateList.size();
        int stepsize = gateList.get(0).size();

        if (nqubits == 0 || stepsize == 0) {
            return new Gate[0][0];
        }

        Gate[][] answer = new Gate[nqubits][stepsize];
        int i = 0;
        for (List<Gate> circuits : gateList) {
            int j = 0;
            for (Gate gate : circuits) {
                answer[i][j] = gate;
            }
        }
        return answer;
    }

    private static Gate[] getColumn(Gate[][] matrix, int j) {
        int nRows = matrix.length;
        if (nRows == 0) {
            return new Gate[0];
        }
        int nCols = matrix[0].length;
        Gate[] answer = new Gate[nRows];
        for (int i = 0; i < nRows; i++) {
            answer[i] = matrix[i][j];
        }
        return answer;
    }

    private Complex[] applyStep(Gate[] step, Complex[] initial, int nqubits) {
        Complex[][] a = calculateStepMatrix(step);
        Complex[] result = new Complex[initial.length];
        for (int i = 0; i < initial.length; i++) {
            result[i] = Complex.ZERO;
            for (int j = 0; j < initial.length; j++) {
                result[i] = result[i].add(a[i][j].mul(initial[j]));
            }
        }
        return result;
    }

    private Complex[][] calculateStepMatrix(Gate[] step) {
        int nqubits = step.length;
        int dim = 1 << nqubits;
          int cnotidx = hasGate(step, Gate.CNOT);
        if (cnotidx > -1) {
            int controlidx = hasGate(step, Gate.C0);
            if (controlidx < 0) {
                throw new IllegalArgumentException("CNOT gate without a control qubit, won't work");
            }
            if (controlidx == cnotidx - 1) {
                System.out.println("OK");
                // ok
            } else {
                if (controlidx > cnotidx) {
                    System.out.println("NOK");
                }
            }
        }
        Complex[] result = new Complex[dim];
        Complex[][] a = step[0].getMatrix(); //getGate(step.get(0).getType());
        int idx = a.length >> 1;
        while (idx < nqubits) {
            Gate g = step[idx];
            
            int qftn = 0;
            if (g.equals(Gate.QFT)) {
                int qi = idx;
                while ((qi < nqubits) && (step[qi].equals(Gate.QFT))){
                    qftn++;
                }
            }
            Complex[][] gate = (g.equals(Gate.QFT)) ? getQftMatrix(qftn): g.getMatrix(); //getGate(step.get(i).getType());
            a = tensor(a, gate);
            idx = idx + (gate.length >> 1);
        }
        return a;
    }
    
    private static Complex[][] getQftMatrix(int n) {
        int size = 1 << n;
        double r = Math.cos(2* Math.PI/size);
        double i = Math.sin(2* Math.PI/size);
        Complex omega = new Complex(r,i);
        Complex[][] answer = new Complex[size][size];
        Complex o = Complex.ONE;
        for (int idx = 0; idx < size*size; idx++) {
            int row = 0;
            while (row < size) {
                int col = 0;
                while (col < size) {
                    if (row * col == size) {
                        answer[row][col] = o;
                    } else if ((row * col) > idx) {
                        col = size;
                    }
                    col++;
                }
                row ++;
            }
            o = o.mul(omega);
        }
        return answer;
    }
    
    /**
     * Check if there is a target in the gates for a specific qubit
     *
     * @param gates
     * @return the first index of the target gate, -1 if there is no such gate
     */
    private int hasGate(Gate[] gates, Gate target) {
        int idx = 0;
        for (Gate gate : gates) {
            if (gate.equals(target)) {
                return idx;
            }
            idx++;
        }
        return -1;
    }
    
    

    @Override
    public double[] calculateQubitStates(Model m) {
        int nq = m.getNQubits();
        Complex[] vectorresult = calculateResults(m);
        return calculateQubitStatesFromVector(vectorresult);
    }

    private double[] calculateQubitStatesFromVector(Complex[] vectorresult) {
        int nq = (int) Math.round(Math.log(vectorresult.length) / Math.log(2));
        System.out.println("nq = "+nq);
        double[] answer = new double[nq];
        int ressize = 1 << nq;
        for (int i = 0; i < nq; i++) {
            int pw = nq - i - 1;
            int div = 1 << pw;
            for (int j = 0; j < ressize; j++) {
                int p1 = j / div;
                if (p1 % 2 == 1) {
                    answer[i] = answer[i] + vectorresult[j].abssqr();
                }
            }
        }
        return answer;
    }

    public static void main(String[] args) {
//        Model model = Model.getInstance();
        //  LocalSimulator sim = new LocalSimulator();
        //    swap();
//        simple1();
//        not1();
//        hadamard1();
//        notnot1();
//        hhnot1();
//        simple2();
//        not2();
//        
    }

    private static void swap() {
        Model model = Model.getInstance();
        model.setNQubits(3);
        System.out.println("set circuit 0");
        model.setGatesForCircuit(0, List.of(Gate.IDENTITY, Gate.IDENTITY));
        System.out.println("set circuit 1");
        model.setGatesForCircuit(1, List.of(Gate.NOT, Gate.SWAP));
        System.out.println("set circuit 2");
        model.setGatesForCircuit(2, List.of(Gate.IDENTITY, Gate.IDENTITY));
//        double[] res = sim.calculateResults(model);
//        System.out.println("SIMPLE res length should be 3: "+res.length);
//        printResults(res);
        LocalSimulator sim = new LocalSimulator();

        double[] states = sim.calculateQubitStates(model);
        printResults2(states);
    }

    private static void simple1() {
        System.out.println("1 qubit, no gate");
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(1);
        model.setGatesForCircuit(0, List.of(Gate.IDENTITY));
        Complex[] res = sim.calculateResults(model);
        System.out.println("SIMPLE res length should be 2: " + res.length);
        printResults(res);
        double[] states = sim.calculateQubitStates(model);
        printResults2(states);
    }
//
//    private static void not1() {
//        LocalSimulator sim = new LocalSimulator();
//        Model model = Model.getInstance();
//        model.setNQubits(1);
//        model.setGatesForCircuit(0, List.of(Gate.NOT));
//        double[] res = sim.calculateResults(model);
//        System.out.println("NOT res length should be 2: " + res.length);
//        printResults(res);
//        double[] states = sim.calculateQubitStates(model);
//        printResults2(states);
//    }
//
//    private static void hadamard1() {
//        System.out.println("Hadamard");
//        LocalSimulator sim = new LocalSimulator();
//        Model model = Model.getInstance();
//        model.setNQubits(1);
//        model.setGatesForCircuit(0, List.of(Gate.HADAMARD));
//        double[] res = sim.calculateResults(model);
//        System.out.println("H res length should be 2: " + res.length);
//        printResults(res);
//        double[] states = sim.calculateQubitStates(model);
//        printResults2(states);
//    }
//
//    private static void notnot1() {
//        System.out.println("notnot");
//        LocalSimulator sim = new LocalSimulator();
//        Model model = Model.getInstance();
//        model.setNQubits(1);
//        List<Gate> gates = List.of(Gate.NOT, Gate.NOT);
//        model.setGatesForCircuit(0, gates);
//        double[] res = sim.calculateResults(model);
//        System.out.println("not not length should be 2: " + res.length);
//        printResults(res);
//        double[] states = sim.calculateQubitStates(model);
//        printResults2(states);
//    }
//
//    private static void hhnot1() {
//        System.out.println("hhnot");
//        LocalSimulator sim = new LocalSimulator();
//        Model model = Model.getInstance();
//        model.setNQubits(1);
//        List gates
//                = List.of(Gate.HADAMARD, Gate.HADAMARD, Gate.NOT);
//        model.setGatesForCircuit(0, gates);
//        double[] res = sim.calculateResults(model);
//        System.out.println("hhnot length should be 2: " + res.length);
//        printResults(res);
//        double[] states = sim.calculateQubitStates(model);
//        printResults2(states);
//    }
//
//    private static void simple2() {
//        System.out.println("2 qubits, no gate");
//        LocalSimulator sim = new LocalSimulator();
//        Model model = Model.getInstance();
//        model.setNQubits(2);
//        GateConfig gates = GateConfig.of(List.of(Gate.IDENTITY), List.of(Gate.IDENTITY));
//        model.setGates(gates);
//        double[] res = sim.calculateResults(model);
//        System.out.println("SIMPLE res length should be 4: " + res.length);
//        printResults(res);
//        double[] states = sim.calculateQubitStates(model);
//        printResults2(states);
//    }
//
//    private static void not2() {
//        System.out.println("2 qubits, not-I gate");
//        LocalSimulator sim = new LocalSimulator();
//        Model model = Model.getInstance();
//        model.setNQubits(2);
//        GateConfig gates = GateConfig.of(List.of(Gate.NOT), List.of(Gate.IDENTITY));
//        model.setGates(gates);
//        double[] res = sim.calculateResults(model);
//        System.out.println("SIMPLE res length should be 4: " + res.length);
//        printResults(res); // should be {0,0,1,0}
//        double[] states = sim.calculateQubitStates(model);
//        printResults2(states);
//    }

    private static void printResults(Complex[] res) {
        for (int i = 0; i < res.length; i++) {
            System.out.println("r[" + i + "]: " + res[i]);
        }
    }
    
    private static void printResults(double[] res) {
        for (int i = 0; i < res.length; i++) {
            System.out.println("r[" + i + "]: " + res[i]);
        }
    }

    private static void printResults2(double[] res) {
        for (int i = 0; i < res.length; i++) {
            System.out.println("q[" + i + "]: " + res[i]);
        }
    }
}
