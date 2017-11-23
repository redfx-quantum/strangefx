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

public enum Gate {

    IDENTITY( "I", GateGroup.IDENTITY,      new double[][]{{1,0}, {0,1}}),
    NOT     ( "X", GateGroup.BIT_FLIP,      new double[][]{{0,1}, {1,0}}),
    HADAMARD( "H", GateGroup.SUPERPOSITION, new double[][]{{ Const.HV, Const.HV }, { Const.HV, -Const.HV }}),
    SWAP    ( "S", GateGroup.BIT_FLIP,      new double[][]{{1,0,0,0},{0,0,1,0},{0,1,0,0},{0,0,0,1}}),
    CNOT    ( "C", GateGroup.BIT_FLIP,      new double[][]{{1,0,0,0},{0,1,0,0},{0,0,0,1},{0,0,1,0}}),
    C0    ( "C0", GateGroup.BIT_FLIP,      new double[][]{{}});
   
    private final String caption;
    private double[][] matrix;
    private final GateGroup group;

    Gate(String caption, GateGroup group, double[][] matrix) {
        this.caption = caption;
        this.matrix = matrix;
        this.group = group;
    }

    public String getCaption() {
        return caption;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public GateGroup getGroup() {
        return group;
    }

    // direct static values cannot be used by enum
    private static class Const {
        private static double HV = 1./Math.sqrt(2.);
    }
}
