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
import com.gluonhq.strange.math.Complex;
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
        Complex[] res = sim.calculateResults(model);
        assertEquals( 2, res.length );
        assertEquals(1.d, res[0].r, DELTA);
        assertEquals(0.d, res[1].r, DELTA);
        assertEquals(0.d, res[0].i, DELTA);
        assertEquals(0.d, res[1].i, DELTA);
    }

    @Test
    @DisplayName("Not gate")
    void not1() {
        Model model = Model.getInstance();
        model.setNQubits(1);
        model.setGatesForCircuit(0, List.of(Gate.NOT));
        Complex[] res = sim.calculateResults(model);
        assertEquals( 2, res.length );
        assertEquals(0.d, res[0].r, DELTA);
        assertEquals(1.d, res[1].r, DELTA);
        assertEquals(0.d, res[0].i, DELTA);
        assertEquals(0.d, res[1].i, DELTA);

    }

    @Test
    @DisplayName("Hadamard gate")
    void hadamard1() {
        Model model = Model.getInstance();
        model.setNQubits(1);
        model.setGatesForCircuit(0,List.of(Gate.HADAMARD));
        Complex[] res = sim.calculateResults(model);
        assertEquals( 2, res.length );
        assertEquals(SQRT2, res[0].r, DELTA);
        assertEquals(SQRT2, res[1].r, DELTA);
        assertEquals(0.d, res[0].i, DELTA);
        assertEquals(0.d, res[1].i, DELTA);
    }

    @Test
    @DisplayName("Not Not gates")
    void notnot1() {
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(1);
        List<Gate> gates = List.of(Gate.NOT, Gate.NOT);
        model.setGatesForCircuit(0, gates);
        Complex[] res = sim.calculateResults(model);
        assertEquals( 2, res.length );
        assertEquals(1.d, res[0].r, DELTA);
        assertEquals(0.d, res[1].r, DELTA);
        assertEquals(0.d, res[0].i, DELTA);
        assertEquals(0.d, res[1].i, DELTA);
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
        Complex[] res = sim.calculateResults(model);
        assertEquals( 2, res.length );
        assertEquals(0.d, res[0].r, DELTA);
        assertEquals(1.d, res[1].r, DELTA);
        assertEquals(0.d, res[0].i, DELTA);
        assertEquals(0.d, res[1].i, DELTA);
    }

    @Test
    @DisplayName("Two qubits, no gates")
    void simple2() {
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(2);
        GateConfig gates = GateConfig.of(List.of(Gate.IDENTITY), List.of(Gate.IDENTITY));
        model.setGates(gates);
        Complex[] res = sim.calculateResults(model);
        assertEquals( 4, res.length );
        assertEquals(1.d, res[0].r, DELTA);
        assertEquals(0.d, res[1].r, DELTA);
        assertEquals(0.d, res[2].r, DELTA);
        assertEquals(0.d, res[3].r, DELTA);
        assertEquals(0.d, res[0].i, DELTA);
        assertEquals(0.d, res[1].i, DELTA);
        assertEquals(0.d, res[2].i, DELTA);
        assertEquals(0.d, res[3].i, DELTA);

    }

    @Test
    @DisplayName("Two qubits, not-I gate")
    void not2() {
        LocalSimulator sim = new LocalSimulator();
        Model model = Model.getInstance();
        model.setNQubits(2);
        GateConfig gates = GateConfig.of(List.of(Gate.NOT), List.of(Gate.IDENTITY));
        model.setGates(gates);
        Complex[] res = sim.calculateResults(model);
        assertEquals( 4, res.length );

        assertEquals(0.d, res[0].r, DELTA);
        assertEquals(0.d, res[1].r, DELTA);
        assertEquals(1.d, res[2].r, DELTA);
        assertEquals(0.d, res[3].r, DELTA);
        assertEquals(0.d, res[0].i, DELTA);
        assertEquals(0.d, res[1].i, DELTA);
        assertEquals(0.d, res[2].i, DELTA);
        assertEquals(0.d, res[3].i, DELTA);
    }
    
    @Test
    @DisplayName("I gate")
    public void unmarshalI() {
        String s = "[[I]]";
        Gate[][] gates = Gate.toMatrix(s);
        LocalSimulator sim = new LocalSimulator();
        Complex[] res = sim.calculateResults(gates);
        assertEquals(res.length, 2);    
        assertEquals(1.0d, res[0].r, DELTA);
        assertEquals(0.0d, res[1].r, DELTA);
        assertEquals(0.d, res[0].i, DELTA);
        assertEquals(0.d, res[1].i, DELTA);
    }   
    
    @Test
    @DisplayName("XHX qubits")
    public void unmarshalIXH() {
        String s = "[[X,H,X]]";
        Gate[][] gates = Gate.toMatrix(s);
        LocalSimulator sim = new LocalSimulator();
        Complex[] results = sim.calculateResults(gates);
        assertEquals(results.length, 8);
        assertEquals(0.0d, results[0].r, DELTA);
        assertEquals(0.0d, results[1].r, DELTA);
        assertEquals(0.0d, results[2].r, DELTA);
        assertEquals(0.0d, results[3].r, DELTA);
        assertEquals(0.0d, results[4].r, DELTA);
        assertEquals(SQRT2, results[5].r, DELTA);
        assertEquals(0.0d, results[6].r, DELTA);
        assertEquals(SQRT2, results[7].r, DELTA);
    }

//    @Test
//    @DisplayName("CNOT qubits")
//    public void unmarshalDX() {
//        String s = "[[C0,C]]";
//        Gate[][] gates = Gate.toMatrix(s);
//        LocalSimulator sim = new LocalSimulator();
//        double[] results = sim.calculateResults(gates);
//        assertEquals(results.length, 4);
//        assertEquals(1.0d, results[0], DELTA);
//        assertEquals(0.0d, results[1], DELTA);
//        assertEquals(0.0d, results[2], DELTA);
//        assertEquals(0.0d, results[3], DELTA);
//    }

    @Test
    @DisplayName("CNOT qubits")
    public void unmarshalXD() {
        String s = "[[C0,C]]";
        Gate[][] gates = Gate.toMatrix(s);
        LocalSimulator sim = new LocalSimulator();
        Complex[] results = sim.calculateResults(gates);
        assertEquals(results.length, 4);
        assertEquals(1.0d, results[0].r, DELTA);
        assertEquals(0.0d, results[1].r, DELTA);
        assertEquals(0.0d, results[2].r, DELTA);
        assertEquals(0.0d, results[3].r, DELTA);
    }
    
    @Test
    @DisplayName("NOTCNOT qubits")
    public void unmarshalXXD() {
        System.out.println("NOTCNOT");
        String s = "[[X,I][C0,C]]";
        Gate[][] gates = Gate.toMatrix(s);
        LocalSimulator sim = new LocalSimulator();
        Complex[] results = sim.calculateResults(gates);
        assertEquals(results.length, 4);
        assertEquals(0.0d, results[0].r, DELTA);
        assertEquals(0.0d, results[1].r, DELTA);
        assertEquals(0.0d, results[2].r, DELTA);
        assertEquals(1.0d, results[3].r, DELTA);
    }
    
}
