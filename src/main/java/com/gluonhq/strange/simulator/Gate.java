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
