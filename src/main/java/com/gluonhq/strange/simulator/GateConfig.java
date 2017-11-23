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
package com.gluonhq.strange.simulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

// to simplify definition of gate matrix
public class GateConfig extends ArrayList<List<Gate>> {

    /**
     * Create a new GateConfig for <code>n</code> circuits
     * @param n
     * @return an initial GateConfig, containing <code>n</code> circuits 
     */
    public static GateConfig initial(int n) {
        GateConfig answer = new GateConfig();
        for (int i = 0; i < n; i++) {
            answer.add(List.of(Gate.IDENTITY));
        }
        return answer;
    }
    
    public static GateConfig of(List<Gate> gates) {
        GateConfig answer = new GateConfig();
        answer.add(Objects.requireNonNull(gates));
        return answer;
    }

    public static GateConfig of(List<Gate> gate, List<Gate>... gates) {
        GateConfig matrix = GateConfig.of(gate);
        matrix.addAll(Arrays.asList(gates) );
        return matrix;
    }

//    public static GateConfig of(Gate gate) {
//        return GateConfig.of(List.of(Objects.requireNonNull(gate)));
//    }
//
//    public static GateConfig of(Gate gate, Gate... gates) {
//        GateConfig gateConfig = GateConfig.of(gate);
//        gateConfig.get(0).addAll(Arrays.asList(gates));
//        return gateConfig;
//    }
}
