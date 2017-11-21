package com.gluonhq.strange.simulator;

import java.util.Objects;
import java.util.Optional;

public enum Gate {

    IDENTITY( "I", new double[][]{{1,0}, {0,1}}, GateGroup.IDENTITY ),
    NOT     ( "X", new double[][]{{0,1}, {1,0}},  GateGroup.BIT_FLIP),
    HADAMARD( "H", new double[][]{{ Const.HV, Const.HV }, { Const.HV, -Const.HV }}, GateGroup.SUPERPOSITION ),
    SWAP("S", new double[][]{{1,0,0,0},{0,0,1,0},{0,1,0,0},{0,0,0,1}}, GateGroup.BIT_FLIP),
    CNOT("C", new double[][]{{1,0,0,0},{0,1,0,0},{0,0,0,1},{0,0,1,0}}, GateGroup.BIT_FLIP);
   
    private final String caption;
    private double[][] matrix;
    private final GateGroup group;

    Gate(String caption, double[][] matrix, GateGroup group ) {
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
