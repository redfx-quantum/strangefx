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
package com.gluonhq.strange.simulator.local;

import com.gluonhq.strange.Model;
import com.gluonhq.strange.simulator.Gate;
import com.gluonhq.strange.simulator.GateConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Local simulator tests")
class LocalSimulatorTests {

    private final LocalSimulator sim = new LocalSimulator();
    private final double DELTA = .0001;
    private final double SQRT2 = Math.sqrt(2.d)/2.d;

    @Test
    @DisplayName("Simple: One qubit, no gates")
    void simple1() {

        Model model = Model.getInstance();
        model.setNQubits(1);
        model.setGatesForCircuit(0, List.of(Gate.IDENTITY));
        double[] res = sim.calculateResults(model);
        assertEquals( 2, res.length );
        assertEquals(1.d, res[0], DELTA);
        assertEquals(0.d, res[1], DELTA);

    }

    @Test
    @DisplayName("Not gate")
    void not1() {
        Model model = Model.getInstance();
        model.setNQubits(1);
        model.setGatesForCircuit(0, List.of(Gate.NOT));
        double[] res = sim.calculateResults(model);
        assertEquals( 2, res.length );
        assertEquals(0.d, res[0], DELTA);
        assertEquals(1.d, res[1], DELTA);

    }

    @Test
    @DisplayName("Hadamard gate")
    void hadamard1() {
        Model model = Model.getInstance();
        model.setNQubits(1);
        model.setGatesForCircuit(0,List.of(Gate.HADAMARD));
        double[] res = sim.calculateResults(model);
        assertEquals( 2, res.length );
        assertEquals(SQRT2, res[0], DELTA);
        assertEquals(SQRT2, res[1], DELTA);
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
        assertEquals(1.d, res[0], DELTA);
        assertEquals(0.d, res[1], DELTA);
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
        assertEquals(0.d, res[0], DELTA);
        assertEquals(1.d, res[1], DELTA);
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
        assertEquals(1.d, res[0], DELTA);
        assertEquals(0.d, res[1], DELTA);
        assertEquals(0.d, res[2], DELTA);
        assertEquals(0.d, res[3], DELTA);

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

        assertEquals(0.d, res[0], DELTA);
        assertEquals(0.d, res[1], DELTA);
        assertEquals(1.d, res[2], DELTA);
        assertEquals(0.d, res[3], DELTA);
    }


}
