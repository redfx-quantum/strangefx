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
