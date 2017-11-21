/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gluonhq.strange.simulator.local;

import com.gluonhq.strange.simulator.Gate;
import com.gluonhq.strange.simulator.GateConfig;
import com.gluonhq.strange.simulator.Simulator;
import com.gluonhq.strange.Model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author johan
 */
public class LocalSimulator implements Simulator {

    private final Model model = Model.getInstance();
    public LocalSimulator() {
        model.refreshRequest().addListener((obs, oldv, newv) -> {
            if (newv) {
                double[] res2 = calculateQubitStates(model);
                printResults2(res2);
                List<Double> reslist = new ArrayList<>();
                for (double d: res2) {reslist.add(d);}
                model.getEndStates().setAll(reslist);
                System.out.println("endstates = "+model.getEndStates());
                model.refreshRequest().set(false);

            }
        });
    }
    @Override
    public double[] calculateResults(Model m) {
        System.out.println("Calculate results for "+m.getNQubits()+" qubits and "+m.getNumberOfSteps()+" steps");
        m.printGates();
        int n = m.getNQubits();
        int v = 1<<n;
        double[] result = new double[v];
        result[0] = 1;
        for (int i = 0; i < m.getNumberOfSteps(); i++) {
            System.out.println("--- apply step "+i+" with gates "+m.getGatesByStep(i));
            result = applyStep(m.getGatesByStep(i), result, n);
            System.out.println("--- applied step "+i);
        }
        return result;
    }
    private double[][] tensor (double[][] a, double[][]b) {
        int d1 = a.length;
        int d2 = b.length;
        double[][] result = new double[d1*d2][d1*d2];
           for (int rowa = 0; rowa <d1; rowa ++) {
                for (int cola = 0; cola <d1; cola++) {
                    for (int rowb = 0; rowb < d2;rowb++) {
                        for (int colb = 0; colb < d2; colb++) {
                            result[d2*rowa+rowb][d2*cola+colb] = a[rowa][cola]*b[rowb][colb];
                        }
                    }
                }
            }
           return result;
    }
    
    private double[] applyStep(List<Gate> step, double[] initial, int nqubits) {
        double[] result = new double[initial.length];
        double[][] a =  step.get(0).getMatrix(); //getGate(step.get(0).getType());
        int idx = a.length >>1;
        while ( idx < nqubits) {
            double[][] m = new double[4<<idx][4<<idx];
            double[][] gate = step.get(idx).getMatrix(); //getGate(step.get(i).getType());
            if (gate.length != 2) {
                throw new RuntimeException ("complex gates that are not on first circuit not yet implemented");
            } 
            a = tensor(a,gate);
            idx++;
        }
        for (int i = 0; i < initial.length; i++) {
            result[i] = 0;
            for (int j = 0 ; j < initial.length; j++) {
                result[i] = result[i] + a[i][j]*initial[j];
            }
        }
        return result;
    }
    
    @Override
    public double[] calculateQubitStates(Model m) {
        int nq = m.getNQubits();
        double[] answer = new double[nq];
        double[] vectorresult = calculateResults(m);
        int ressize = 1<<nq;
        for (int i = 0; i < nq; i++) {
            int pw = nq-i-1;
            int div = 1 << pw;
            for (int j = 0; j < ressize; j++) {
                int p1 = j / div;
                if (p1 % 2 == 1) {
                    answer[i] = answer[i] + vectorresult[j] * vectorresult[j];
                }
            }
        }
        return answer;
    }
    
//    private double[][] getGate(int g) {
//        double[][]answer = new double[2][2];
//
//        if (Model.GATE_NOT == g) {
//            answer[0][1] = 1;
//            answer[1][0] = 1;
//            return answer;
//        }
//        if (Model.GATE_HADAMARD == g) {
//            double s2 = 1./Math.sqrt(2.);
//            answer[0][0] = s2;
//            answer[0][1] = s2;
//            answer[1][0] = s2;
//            answer[1][1] = -s2;
//            return answer;
//        }
//        answer[0][0] = 1;
//        answer[1][1] = 1;
//        return answer;
//    }
    
    public static void main(String[] args) {
        Model model = Model.getInstance();
        LocalSimulator sim = new LocalSimulator();
        swap();
        simple1();
        not1();
        hadamard1();
        notnot1();
        hhnot1();
        simple2();
        not2();
//        

//        model.setNQubits(2);
//        res = sim.calculateResults(model);
//        System.out.println("res length should be 4: "+res.length);
//        printResults(res);
//
//        model.setNQubits(3);
//        res = sim.calculateResults(model);
//        System.out.println("res length should be 8: "+res.length);
//        printResults(res);

    }
    
             
    private static void swap() {
        System.out.println("1 qubit, no gate");
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(2);
        model.setGatesForCircuit(0, List.of(Gate.NOT, Gate.SWAP));
        double[] res = sim.calculateResults(model);
        System.out.println("SIMPLE res length should be 2: "+res.length);
        printResults(res);
        double[] states = sim.calculateQubitStates(model);
        printResults2(states);
    }
       
    private static void simple1() {
        System.out.println("1 qubit, no gate");
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(1);
        model.setGatesForCircuit(0, List.of(Gate.IDENTITY));
        double[] res = sim.calculateResults(model);
        System.out.println("SIMPLE res length should be 2: "+res.length);
        printResults(res);
        double[] states = sim.calculateQubitStates(model);
        printResults2(states);
    }
    
    private static void not1() {
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(1);
        model.setGatesForCircuit(0, List.of(Gate.NOT));
        double[] res = sim.calculateResults(model);
        System.out.println("NOT res length should be 2: "+res.length);
        printResults(res);
        double[] states = sim.calculateQubitStates(model);
        printResults2(states);
    }
        
    private static void hadamard1() {
        System.out.println("Hadamard");
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(1);
        model.setGatesForCircuit(0,List.of(Gate.HADAMARD));
        double[] res = sim.calculateResults(model);
        System.out.println("H res length should be 2: "+res.length);
        printResults(res);
        double[] states = sim.calculateQubitStates(model);
        printResults2(states);
    }
            
    private static void notnot1() {
        System.out.println("notnot");
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(1);
        List<Gate> gates = List.of(Gate.NOT, Gate.NOT);
        model.setGatesForCircuit(0, gates);
        double[] res = sim.calculateResults(model);
        System.out.println("not not length should be 2: "+res.length);
        printResults(res);
        double[] states = sim.calculateQubitStates(model);
        printResults2(states);
    }
                
    private static void hhnot1() {
        System.out.println("hhnot");
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(1);
        List gates = 
            List.of(Gate.HADAMARD,Gate.HADAMARD,Gate.NOT);
        model.setGatesForCircuit(0,gates);
        double[] res = sim.calculateResults(model);
        System.out.println("hhnot length should be 2: "+res.length);
        printResults(res);
        double[] states = sim.calculateQubitStates(model);
        printResults2(states);
    }
            
    private static void simple2() {
        System.out.println("2 qubits, no gate");
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(2);
        GateConfig gates = GateConfig.of(List.of(Gate.IDENTITY), List.of(Gate.IDENTITY));
        model.setGates(gates);
        double[] res = sim.calculateResults(model);
        System.out.println("SIMPLE res length should be 4: "+res.length);
        printResults(res);
        double[] states = sim.calculateQubitStates(model);
        printResults2(states);
    }
                
    private static void not2() {
        System.out.println("2 qubits, not-I gate");
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(2);
        GateConfig gates = GateConfig.of(List.of(Gate.NOT), List.of(Gate.IDENTITY));
        model.setGates(gates);
        double[] res = sim.calculateResults(model);
        System.out.println("SIMPLE res length should be 4: "+res.length);
        printResults(res); // should be {0,0,1,0}
        double[] states = sim.calculateQubitStates(model);
        printResults2(states);
    }
    
    private static void printResults (double[] res) {
        for (int i = 0; i < res.length; i++) {
            System.out.println("r["+i+"]: "+res[i] );
        }
    }
    
        
    private static void printResults2 (double[] res) {
        for (int i = 0; i < res.length; i++) {
            System.out.println("q["+i+"]: "+res[i] );
        }
    }
}


