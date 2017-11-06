package com.gluonhq.strange.simulator;

public enum Gate {

    NOGATE(      "", new double[][]{{1,0}, {0,1}} ),
    NOT(      "NOT", new double[][]{{0,1}, {1,0}}),
    HADAMARD(   "H", new double[][]{{ getHv(),getHv()}, {getHv(), -getHv()}});

    private static double hv = 1./Math.sqrt(2.);

    private final String caption;
    private double[][] matrix;

    private static double getHv() {return hv;}

    Gate(String caption, double[][] matrix ) {
        this.caption = caption;
        this.matrix = matrix;
    }

    public String getCaption() {
        return caption;
    }

    public double[][] getMatrix() {
        return matrix;
    }
}
