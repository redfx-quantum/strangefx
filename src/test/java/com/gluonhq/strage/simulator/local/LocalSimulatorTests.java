package com.gluonhq.strage.simulator.local;

import com.gluonhq.strange.Model;
import com.gluonhq.strange.simulator.Gate;
import com.gluonhq.strange.simulator.GateConfig;
import com.gluonhq.strange.simulator.local.LocalSimulator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocalSimulatorTests {

    private final LocalSimulator sim = new LocalSimulator();


    @Test
    void simple1() {

        System.out.println("Test: 1 qubit, no gate");

        Model model = Model.getInstance();
        model.setNQubits(1);
        model.setGatesForCircuit(0, List.of(Gate.IDENTITY));

        double[] res = sim.calculateResults(model);
        assertEquals( 2, res.length );

//        System.out.println("SIMPLE res length should be 2: "+res.length);
//        printResults(res);
//        double[] states = sim.calculateQubitStates(model);
//        printResults2(states);
    }

    @Test
    void not1() {
        Model model = Model.getInstance();
        model.setNQubits(1);
        model.setGatesForCircuit(0, List.of(Gate.NOT));
        double[] res = sim.calculateResults(model);

        assertEquals( 2, res.length );

//        System.out.println("NOT res length should be 2: "+res.length);
//        printResults(res);
//        double[] states = sim.calculateQubitStates(model);
//        printResults2(states);
    }

    @Test
    void hadamard1() {
        System.out.println("Test: Hadamard");
        Model model = Model.getInstance();
        model.setNQubits(1);
        model.setGatesForCircuit(0,List.of(Gate.HADAMARD));
        double[] res = sim.calculateResults(model);

        assertEquals( 2, res.length );

//        System.out.println("H res length should be 2: "+res.length);
//        printResults(res);
//        double[] states = sim.calculateQubitStates(model);
//        printResults2(states);
    }

    @Test
    void notnot1() {
        System.out.println("Test: notnot");
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(1);
        List<Gate> gates = List.of(Gate.NOT, Gate.NOT);
        model.setGatesForCircuit(0, gates);
        double[] res = sim.calculateResults(model);

        assertEquals( 2, res.length );

//        System.out.println("not not length should be 2: "+res.length);
//        printResults(res);
//        double[] states = sim.calculateQubitStates(model);
//        printResults2(states);
    }

    @Test
    void hhnot1() {
        System.out.println("Test: hhnot");
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(1);
        List gates = List.of(Gate.HADAMARD,Gate.HADAMARD,Gate.NOT);
        model.setGatesForCircuit(0,gates);
        double[] res = sim.calculateResults(model);

        assertEquals( 2, res.length );

//        System.out.println("hhnot length should be 2: "+res.length);
//        printResults(res);
//        double[] states = sim.calculateQubitStates(model);
//        printResults2(states);
    }

    @Test
    void simple2() {
        System.out.println("Test: 2 qubits, no gate");
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(2);
        GateConfig gates = GateConfig.of(List.of(Gate.IDENTITY), List.of(Gate.IDENTITY));
        model.setGates(gates);
        double[] res = sim.calculateResults(model);

        assertEquals( 4, res.length );

//        System.out.println("SIMPLE res length should be 4: "+res.length);
//        printResults(res);
//        double[] states = sim.calculateQubitStates(model);
//        printResults2(states);
    }

    @Test
    void not2() {
        System.out.println("Test: 2 qubits, not-I gate");
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(2);
        GateConfig gates = GateConfig.of(List.of(Gate.NOT), List.of(Gate.IDENTITY));
        model.setGates(gates);
        double[] res = sim.calculateResults(model);

        assertEquals( 4, res.length );

//        System.out.println("SIMPLE res length should be 4: "+res.length);
//        printResults(res); // should be {0,0,1,0}
//        double[] states = sim.calculateQubitStates(model);
//        printResults2(states);
    }


}
