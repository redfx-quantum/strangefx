/*-
 * #%L
 * StrangeFX
 * %%
 * Copyright (C) 2020 Johan Vos
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Johan Vos nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.redfx.strangefx.reder;

import java.util.List;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.redfx.strange.Gate;


import org.redfx.strange.Program;
import org.redfx.strange.Step;
import org.redfx.strange.gate.X;
import org.redfx.strangefx.simulator.RenderModel;
import org.redfx.strangefx.ui.GateSymbol;

import org.redfx.strangefx.ui.QubitBoard;
import org.redfx.strangefx.ui.QubitFlow;

@DisplayName("Rendertests")
class RenderTests {

    private void startFX() {
        System.err.println("Start JavaFX framework...");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                System.err.println("JavaFX Platform initialized");
            }
        };
        try {
            System.err.println("Startup JavaFX Platform");
            Platform.startup(r);
            System.err.println("Startup JavaFX Platform done");
        } catch (java.lang.IllegalStateException e) {
            System.err.println("Toolkit already initialized, ignore");
        }
    
    }
    
    @Test
    @DisplayName("createQubitBoard") 
    void createQubitBoard() {
        System.err.println("CreateQubitBoard");
        RenderModel model = new RenderModel();
        QubitBoard qb = new QubitBoard(model);
    }
    
    @Test
    void modelProgramConstructor() {
        int origQubits = 3;
        Program p = new Program(origQubits);
        RenderModel model = new RenderModel(p);
        int modelQubits = model.getNQubits();
        assertEquals(origQubits, modelQubits, "Model and created program need to have same amount of qubits");
    }
    
    @Test
    void createQubitBoardWithQubits() {
        startFX();
        int origQubits = 3;
        Program p = new Program(origQubits);
        RenderModel model = new RenderModel(p);
        QubitBoard qb = new QubitBoard(model);
    }

    @Test
    void createSingleStep() {
        startFX();
        Program p = new Program(1);
        Step s = new Step();
        s.addGate(new X(0));
        p.addStep(s);
        RenderModel model = new RenderModel(p);
        QubitBoard qb = new QubitBoard(model);
        qb.layout();
        List<QubitFlow> flows = qb.getQubitFlows();
        assertNotNull(flows);
        assertEquals(flows.size(), 1);
        QubitFlow flow = flows.get(0);
        assertEquals(flow.getIndex(), 0);
        Pane gateRow = flow.getGateRow();
        assertNotNull(gateRow);
        assertEquals(gateRow.getChildren().size(), 1);
        Node gateNode = gateRow.getChildren().get(0);
        assertTrue (gateNode instanceof GateSymbol);
        GateSymbol symbol = (GateSymbol)gateNode;
        Gate gate = symbol.getGate();
        assertTrue (gate instanceof X);
    }

    @Test
    void createSingleStepTwoqubits() {
        startFX();
        Program p = new Program(2);
        Step s = new Step();
        s.addGate(new X(1));
        p.addStep(s);
        RenderModel model = new RenderModel(p);
        QubitBoard qb = new QubitBoard(model);
        qb.layout();
        List<QubitFlow> flows = qb.getQubitFlows();
        assertNotNull(flows);
        assertEquals(flows.size(), 2);
        QubitFlow flow = flows.get(1);
        assertEquals(flow.getIndex(), 1);
        Pane gateRow = flow.getGateRow();
        assertNotNull(gateRow);
        assertEquals(gateRow.getChildren().size(), 1);
        Node gateNode = gateRow.getChildren().get(0);
        assertTrue (gateNode instanceof GateSymbol);
        GateSymbol symbol = (GateSymbol)gateNode;
        Gate gate = symbol.getGate();
        System.err.println("GATE = "+gate);
        assertTrue (gate instanceof X);
    }
//    private final LocalSimulator sim = new LocalSimulator(new Model());
//    private final double DELTA = .0001;
//    private final double SQRT2 = Math.sqrt(2.d)/2.d;
//
//    @Test
//    @DisplayName("Simple: One qubit, no gates")
//    void simple1() {
//        Model model = Model.getInstance();
//        model.setNQubits(1);
//        model.setGatesForCircuit(0, List.of(Gate.IDENTITY));
//        Complex[] res = sim.calculateResults(model);
//        assertEquals( 2, res.length );
//        assertEquals(1.d, res[0].r, DELTA);
//        assertEquals(0.d, res[1].r, DELTA);
//        assertEquals(0.d, res[0].i, DELTA);
//        assertEquals(0.d, res[1].i, DELTA);
//    }
//
//    @Test
//    @DisplayName("Not gate")
//    void not1() {
//        Model model = Model.getInstance();
//        model.setNQubits(1);
//        model.setGatesForCircuit(0, List.of(Gate.NOT));
//        Complex[] res = sim.calculateResults(model);
//        assertEquals( 2, res.length );
//        assertEquals(0.d, res[0].r, DELTA);
//        assertEquals(1.d, res[1].r, DELTA);
//        assertEquals(0.d, res[0].i, DELTA);
//        assertEquals(0.d, res[1].i, DELTA);
//
//    }
//
//    @Test
//    @DisplayName("Hadamard gate")
//    void hadamard1() {
//        Model model = Model.getInstance();
//        model.setNQubits(1);
//        model.setGatesForCircuit(0,List.of(Gate.HADAMARD));
//        Complex[] res = sim.calculateResults(model);
//        assertEquals( 2, res.length );
//        assertEquals(SQRT2, res[0].r, DELTA);
//        assertEquals(SQRT2, res[1].r, DELTA);
//        assertEquals(0.d, res[0].i, DELTA);
//        assertEquals(0.d, res[1].i, DELTA);
//    }
//
//    @Test
//    @DisplayName("Not Not gates")
//    void notnot1() {
//        LocalSimulator sim = new LocalSimulator();
//        Model model = Model.getInstance();
//        model.setNQubits(1);
//        List<Gate> gates = List.of(Gate.NOT, Gate.NOT);
//        model.setGatesForCircuit(0, gates);
//        Complex[] res = sim.calculateResults(model);
//        assertEquals( 2, res.length );
//        assertEquals(1.d, res[0].r, DELTA);
//        assertEquals(0.d, res[1].r, DELTA);
//        assertEquals(0.d, res[0].i, DELTA);
//        assertEquals(0.d, res[1].i, DELTA);
//    }
//
//    @Test
//    @DisplayName("Hadamard Not Not gates")
//    void hhnot1() {
//        System.out.println("Test: hhnot");
//        LocalSimulator sim = new LocalSimulator();
//        Model model = Model.getInstance();
//        model.setNQubits(1);
//        List<Gate> gates = List.of(Gate.HADAMARD,Gate.HADAMARD,Gate.NOT);
//        model.setGatesForCircuit(0,gates);
//        Complex[] res = sim.calculateResults(model);
//        assertEquals( 2, res.length );
//        assertEquals(0.d, res[0].r, DELTA);
//        assertEquals(1.d, res[1].r, DELTA);
//        assertEquals(0.d, res[0].i, DELTA);
//        assertEquals(0.d, res[1].i, DELTA);
//    }
//
//    @Test
//    @DisplayName("Two qubits, no gates")
//    void simple2() {
//        LocalSimulator sim = new LocalSimulator();
//        Model model = Model.getInstance();
//        model.setNQubits(2);
//        GateConfig gates = GateConfig.of(List.of(Gate.IDENTITY), List.of(Gate.IDENTITY));
//        model.setGates(gates);
//        Complex[] res = sim.calculateResults(model);
//        assertEquals( 4, res.length );
//        assertEquals(1.d, res[0].r, DELTA);
//        assertEquals(0.d, res[1].r, DELTA);
//        assertEquals(0.d, res[2].r, DELTA);
//        assertEquals(0.d, res[3].r, DELTA);
//        assertEquals(0.d, res[0].i, DELTA);
//        assertEquals(0.d, res[1].i, DELTA);
//        assertEquals(0.d, res[2].i, DELTA);
//        assertEquals(0.d, res[3].i, DELTA);
//
//    }
//
//    @Test
//    @DisplayName("Two qubits, not-I gate")
//    void not2() {
//        LocalSimulator sim = new LocalSimulator();
//        Model model = Model.getInstance();
//        model.setNQubits(2);
//        GateConfig gates = GateConfig.of(List.of(Gate.NOT), List.of(Gate.IDENTITY));
//        model.setGates(gates);
//        Complex[] res = sim.calculateResults(model);
//        assertEquals( 4, res.length );
//
//        assertEquals(0.d, res[0].r, DELTA);
//        assertEquals(0.d, res[1].r, DELTA);
//        assertEquals(1.d, res[2].r, DELTA);
//        assertEquals(0.d, res[3].r, DELTA);
//        assertEquals(0.d, res[0].i, DELTA);
//        assertEquals(0.d, res[1].i, DELTA);
//        assertEquals(0.d, res[2].i, DELTA);
//        assertEquals(0.d, res[3].i, DELTA);
//    }
//    
//    @Test
//    @DisplayName("I gate")
//    public void unmarshalI() {
//        String s = "[[I]]";
//        Gate[][] gates = Gate.toMatrix(s);
//        LocalSimulator sim = new LocalSimulator();
//        Complex[] res = sim.calculateResults(gates);
//        assertEquals(res.length, 2);    
//        assertEquals(1.0d, res[0].r, DELTA);
//        assertEquals(0.0d, res[1].r, DELTA);
//        assertEquals(0.d, res[0].i, DELTA);
//        assertEquals(0.d, res[1].i, DELTA);
//    }   
//    
//    @Test
//    @DisplayName("XHX qubits")
//    public void unmarshalIXH() {
//        String s = "[[X,H,X]]";
//        Gate[][] gates = Gate.toMatrix(s);
//        LocalSimulator sim = new LocalSimulator();
//        Complex[] results = sim.calculateResults(gates);
//        assertEquals(results.length, 8);
//        assertEquals(0.0d, results[0].r, DELTA);
//        assertEquals(0.0d, results[1].r, DELTA);
//        assertEquals(0.0d, results[2].r, DELTA);
//        assertEquals(0.0d, results[3].r, DELTA);
//        assertEquals(0.0d, results[4].r, DELTA);
//        assertEquals(SQRT2, results[5].r, DELTA);
//        assertEquals(0.0d, results[6].r, DELTA);
//        assertEquals(SQRT2, results[7].r, DELTA);
//    }
//
////    @Test
////    @DisplayName("CNOT qubits")
////    public void unmarshalDX() {
////        String s = "[[C0,C]]";
////        Gate[][] gates = Gate.toMatrix(s);
////        LocalSimulator sim = new LocalSimulator();
////        double[] results = sim.calculateResults(gates);
////        assertEquals(results.length, 4);
////        assertEquals(1.0d, results[0], DELTA);
////        assertEquals(0.0d, results[1], DELTA);
////        assertEquals(0.0d, results[2], DELTA);
////        assertEquals(0.0d, results[3], DELTA);
////    }
//
//    @Test
//    @DisplayName("CNOT qubits")
//    public void unmarshalXD() {
//        String s = "[[C0,C]]";
//        Gate[][] gates = Gate.toMatrix(s);
//        LocalSimulator sim = new LocalSimulator();
//        Complex[] results = sim.calculateResults(gates);
//        assertEquals(results.length, 4);
//        assertEquals(1.0d, results[0].r, DELTA);
//        assertEquals(0.0d, results[1].r, DELTA);
//        assertEquals(0.0d, results[2].r, DELTA);
//        assertEquals(0.0d, results[3].r, DELTA);
//    }
//    
//    @Test
//    @DisplayName("NOTCNOT qubits")
//    public void unmarshalXXD() {
//        System.out.println("NOTCNOT");
//        String s = "[[X,I][C0,C]]";
//        Gate[][] gates = Gate.toMatrix(s);
//        LocalSimulator sim = new LocalSimulator();
//        Complex[] results = sim.calculateResults(gates);
//        assertEquals(results.length, 4);
//        assertEquals(0.0d, results[0].r, DELTA);
//        assertEquals(0.0d, results[1].r, DELTA);
//        assertEquals(0.0d, results[2].r, DELTA);
//        assertEquals(1.0d, results[3].r, DELTA);
//    }
//      
//    @Test
//    @DisplayName("QFT2 qubits")
//    public void unmarshalQFT2() {
//        System.out.println("QFT2");
//        String s = "[[QFT,QFT]]";
//        Gate[][] gates = Gate.toMatrix(s);
//        LocalSimulator sim = new LocalSimulator();
//        Complex[] results = sim.calculateResults(gates);
//        assertEquals(results.length, 4);
//        assertEquals(.5d, results[0].r, DELTA);
//        assertEquals(.5d, results[1].r, DELTA);
//        assertEquals(.5d, results[2].r, DELTA);
//        assertEquals(.5d, results[3].r, DELTA);
//    }
//    
//    @Test
//    @DisplayName("QFT2X qubits")
//    public void unmarshalQFT2X() {
//        try {
//        System.out.println("QFT2X");
//        String s = "[[QFT,QFT,X]]";
//        Gate[][] gates = Gate.toMatrix(s);
//        LocalSimulator sim = new LocalSimulator();
//        Complex[] results = sim.calculateResults(gates);
//        assertEquals(results.length, 8);
//        assertEquals(.5d, results[1].r, DELTA);
//        assertEquals(.5d, results[3].r, DELTA);
//        assertEquals(.5d, results[5].r, DELTA);
//        assertEquals(.5d, results[7].r, DELTA);
//        assertEquals(.0d, results[0].r, DELTA);
//        assertEquals(.0d, results[2].r, DELTA);
//        assertEquals(.0d, results[4].r, DELTA);
//        assertEquals(.0d, results[6].r, DELTA);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    @Test
//    @DisplayName("2stepQFT2X qubits")
//    public void unmarshal2stepQFT2X() {
//        System.out.println("2stepQFT2X");
//        String s = "[[X,H,X][QFT,QFT,X]]";
//        Gate[][] gates = Gate.toMatrix(s);
//        LocalSimulator sim = new LocalSimulator();
//        Complex[] results = sim.calculateResults(gates);
//        assertEquals(results.length, 8);
//        assertEquals(SQRT2, results[0].r, DELTA);
//        assertEquals(0.d, results[0].i, DELTA);
//        assertEquals(0.d, results[1].r, DELTA);
//        assertEquals(0.d, results[1].i, DELTA);
//        assertEquals(-.5*SQRT2, results[2].r, DELTA);
//        assertEquals(-.5*SQRT2, results[2].i, DELTA);
//        assertEquals(0.d, results[3].r, DELTA);
//        assertEquals(0.d, results[3].i, DELTA);
//        assertEquals(0.d, results[4].r, DELTA);
//        assertEquals(0.d, results[4].i, DELTA);
//        assertEquals(0.d, results[5].r, DELTA);
//        assertEquals(0.d, results[5].i, DELTA);
//        assertEquals(-.5*SQRT2, results[6].r, DELTA);
//        assertEquals(.5*SQRT2, results[6].i, DELTA);
//        assertEquals(0.d, results[7].r, DELTA);
//        assertEquals(0.d, results[7].i, DELTA);
//    }
    
}
