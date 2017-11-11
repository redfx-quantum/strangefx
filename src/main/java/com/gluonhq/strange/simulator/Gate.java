package com.gluonhq.strange.simulator;

import java.util.Objects;
import java.util.Optional;

public enum Gate {

    IDENTITY( "I", new double[][]{{1,0}, {0,1}} ),
    NOT     ( "X", new double[][]{{0,1}, {1,0}} ),
    HADAMARD( "H", new double[][]{{ Const.HV, Const.HV }, { Const.HV, -Const.HV }} );

    private final String caption;
    private double[][] matrix;

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

    // direct static values cannot be used by enum
    private static class Const {
        private static double HV = 1./Math.sqrt(2.);
    }
}
