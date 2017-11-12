package com.gluonhq.strage.simulator.local;

import com.gluonhq.strange.Model;
import com.gluonhq.strange.simulator.Gate;
import com.gluonhq.strange.simulator.GateConfig;
import com.gluonhq.strange.simulator.local.LocalSimulator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Local simulator tests")
class LocalSimulatorTests {

    private final LocalSimulator sim = new LocalSimulator();


    @Test
    @DisplayName("Simple: One qubit, no gates")
    void simple1() {

        Model model = Model.getInstance();
        model.setNQubits(1);
        model.setGatesForCircuit(0, List.of(Gate.IDENTITY));

        double[] res = sim.calculateResults(model);
        assertEquals( 2, res.length );

    }

    @Test
    @DisplayName("Not gate")
    void not1() {
        Model model = Model.getInstance();
        model.setNQubits(1);
        model.setGatesForCircuit(0, List.of(Gate.NOT));
        double[] res = sim.calculateResults(model);

        assertEquals( 2, res.length );

    }

    @Test
    @DisplayName("Hadamard gate")
    void hadamard1() {
        Model model = Model.getInstance();
        model.setNQubits(1);
        model.setGatesForCircuit(0,List.of(Gate.HADAMARD));
        double[] res = sim.calculateResults(model);

        assertEquals( 2, res.length );

    }

    @Test
    @DisplayName("Not Not gates")
    void notnot1() {
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(1);
        List<Gate> gates = List.of(Gate.NOT, Gate.NOT);
        model.setGatesForCircuit(0, gates);
        double[] res = sim.calculateResults(model);

        assertEquals( 2, res.length );

    }

    @Test
    @DisplayName("Hadamard Not Not gates")
    void hhnot1() {
        System.out.println("Test: hhnot");
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(1);
        List<Gate> gates = List.of(Gate.HADAMARD,Gate.HADAMARD,Gate.NOT);
        model.setGatesForCircuit(0,gates);
        double[] res = sim.calculateResults(model);

        assertEquals( 2, res.length );

    }

    @Test
    @DisplayName("Two qubits, no gates")
    void simple2() {
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(2);
        GateConfig gates = GateConfig.of(List.of(Gate.IDENTITY), List.of(Gate.IDENTITY));
        model.setGates(gates);
        double[] res = sim.calculateResults(model);

        assertEquals( 4, res.length );

    }

    @Test
    @DisplayName("Two qubits, not-I gate")
    void not2() {
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(2);
        GateConfig gates = GateConfig.of(List.of(Gate.NOT), List.of(Gate.IDENTITY));
        model.setGates(gates);
        double[] res = sim.calculateResults(model);

        assertEquals( 4, res.length );

    }


}
