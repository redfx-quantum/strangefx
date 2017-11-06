package com.gluonhq.strange.simulator;

import java.util.Objects;
import java.util.Optional;

public enum Gate {

    IDENTITY( "I", new double[][]{{1,0}, {0,1}} ),
    NOT     ( "NOT", new double[][]{{0,1}, {1,0}} ),
    HADAMARD( "H"  , new double[][]{{ Const.HV, Const.HV }, { Const.HV, -Const.HV }} );

    private static double hv = 1./Math.sqrt(2.);

    private final String caption;
    private double[][] matrix;

    private static double hv() {return hv;}

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

    public static Optional<Gate> byCaption( String caption) {
        Objects.requireNonNull(caption);
        for( Gate g: values()) {
            if ( g.getCaption().equalsIgnoreCase(caption)) return Optional.of(g);
        }
        return Optional.empty();
    }

    // direct static values cannot be used by enum
    private static class Const {
        private static double HV = 1./Math.sqrt(2.);
    }
}
