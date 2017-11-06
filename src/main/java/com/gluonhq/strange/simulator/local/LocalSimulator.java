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

import java.util.List;

/**
 *
 * @author johan
 */
public class LocalSimulator implements Simulator {

    @Override
    public double[] calculateResults(Model m) {
        int n = m.getNQubits();
        int v = 1<<n;
        double[] result = new double[v];
        result[0] = 1;
        System.out.println("result has size "+v);
        for (int i = 0; i < m.getNumberOfSteps(); i++) {
            result = applyStep(m.getStep(i), result);
        }
        return result;
    }
    
    private double[] applyStep(List<Gate> step, double[] initial) {
        double[] result = new double[initial.length];
        double[][] a =  step.get(0).getMatrix(); //getGate(step.get(0).getType());
        for (int i = 1; i < step.size(); i++) {
            double[][] m = new double[4<<i][4<<i];
            double[][] gate = step.get(i).getMatrix(); //getGate(step.get(i).getType());
            for (int row = 0; row < a.length; row ++) {
                for (int col = 0; col < a.length; col++) {
                    m[2*row][2*col] = a[row][col]*gate[0][0];
                    m[2*row][2*col+1] = a[row][col]*gate[0][1];
                    m[2*row+1][2*col] = a[row][col]*gate[1][0];
                    m[2*row+1][2*col+1] = a[row][col]*gate[1][1];
                }
            }
            a = new double[4<<i][4<<i]; // replace with System.arrayCopy
            for (int row = 0; row < a.length;row++) {
                for (int col = 0; col < a.length; col++) {
                    a[row][col] = m[row][col];
                    System.out.println("a["+row+"]["+col+"] = "+a[row][col]);
                }
            }
        }
        for (int i = 0; i < initial.length; i++) {
            result[i] = 0;
            for (int j = 0 ; j < initial.length; j++) {
                System.out.println("a["+i+"]["+j+"] = "+a[i][j]+", initial was "+initial[j]);
                result[i] = result[i] + a[i][j]*initial[j];
                System.out.println("result["+i+"] = "+result[i]);
            }
        }
        return result;
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
        simple1();
        not1();
        hadamard1();
        notnot1();
        hhnot1();
        simple2();
        not2();
        

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
    
        
    private static void simple1() {
        System.out.println("1 qubit, no gate");
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(1);
        model.setGates(GateConfig.of(Gate.NOGATE));
        double[] res = sim.calculateResults(model);
        System.out.println("SIMPLE res length should be 2: "+res.length);
        printResults(res);
    }
    
    private static void not1() {
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(1);
        model.setGates(GateConfig.of(Gate.NOT));
        double[] res = sim.calculateResults(model);
        System.out.println("NOT res length should be 2: "+res.length);
        printResults(res);
    }
        
    private static void hadamard1() {
        System.out.println("Hadamard");
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(1);
        model.setGates(GateConfig.of(Gate.HADAMARD));
        double[] res = sim.calculateResults(model);
        System.out.println("H res length should be 2: "+res.length);
        printResults(res);
    }
            
    private static void notnot1() {
        System.out.println("notnot");
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(1);
        GateConfig gates = GateConfig.of(
            List.of(Gate.NOT),
            List.of(Gate.NOT)
        );
        model.setGates(gates);
        double[] res = sim.calculateResults(model);
        System.out.println("not not length should be 2: "+res.length);
        printResults(res);
    }
                
    private static void hhnot1() {
        System.out.println("hhnot");
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(1);
        GateConfig gates = GateConfig.of(
            List.of(Gate.HADAMARD),
            List.of(Gate.HADAMARD),
            List.of(Gate.NOT)
        );
        model.setGates(gates);
        double[] res = sim.calculateResults(model);
        System.out.println("hhnot length should be 2: "+res.length);
        printResults(res);
    }
            
    private static void simple2() {
        System.out.println("2 qubits, no gate");
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(2);
        GateConfig gates = GateConfig.of(
            Gate.NOGATE, Gate.NOGATE
        );
        model.setGates(gates);
        double[] res = sim.calculateResults(model);
        System.out.println("SIMPLE res length should be 4: "+res.length);
        printResults(res);
    }
                
    private static void not2() {
        System.out.println("2 qubits, not-I gate");
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(2);
        GateConfig gates = GateConfig.of(
            Gate.NOT, Gate.NOGATE
        );
        model.setGates(gates);
        double[] res = sim.calculateResults(model);
        System.out.println("SIMPLE res length should be 4: "+res.length);
        printResults(res);
    }
    private static void printResults (double[] res) {
        for (int i = 0; i < res.length; i++) {
            System.out.println("r["+i+"]: "+res[i] );
        }
    }
    
}


