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
 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.redfx.strange.simulator.local;

import org.redfx.strange.Gate;
import org.redfx.strange.Program;
import org.redfx.strange.Qubit;
import org.redfx.strange.Result;
import org.redfx.strange.Step;
import org.redfx.strange.simulator.Simulator;
import org.redfx.strange.simulator.Model;
import org.redfx.strange.Complex;
import org.redfx.strange.simulator.CloudSimulator;
import org.redfx.strange.local.SimpleQuantumExecutionEnvironment;


import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;

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
            calculate();
            model.refreshRequest().set(false);
        });
    }
    
    private void calculate() {
        int nq = model.getNQubits();
        Program p = new Program(nq);
        int nsteps = model.getNumberOfSteps();
        for (int step = 0; step < nsteps; step++) {
            Step s = new Step();
            Gate[] gate = model.getGatesByStep(step);
            for (Gate g : gate) {
                s.addGate(g);
            }
            p.addStep(s);
            SimpleQuantumExecutionEnvironment sqee = new SimpleQuantumExecutionEnvironment();
            Result res = sqee.runProgram(p);
            Qubit[] qubits = res.getQubits();
            List<Double> probability = new ArrayList<>();

            
            for (Qubit qubit: qubits) {
                probability.add(qubit.getProbability());
            }
            ObservableList<Double> endStates = model.getEndStates();
            endStates.setAll(probability);
        }
    }
    

    @Override
    public Complex[] calculateResults(Gate[][] gates) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Complex[] calculateResults(Model m) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[] calculateQubitStates(Model m) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
