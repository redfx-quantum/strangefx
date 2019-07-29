package com.gluonhq.strangefx.render;

import com.gluonhq.strange.Complex;
import com.gluonhq.strange.Gate;
import com.gluonhq.strange.Program;
import com.gluonhq.strange.QuantumExecutionEnvironment;
import com.gluonhq.strange.Qubit;
import com.gluonhq.strange.Result;
import com.gluonhq.strange.Step;
import com.gluonhq.strange.gate.Identity;
import com.gluonhq.strange.gate.Oracle;
import com.gluonhq.strange.local.SimpleQuantumExecutionEnvironment;
import com.gluonhq.strange.simulator.Model;
import com.gluonhq.strange.ui.GateSymbol;
import com.gluonhq.strange.ui.Main;
import com.gluonhq.strange.ui.QubitBoard;
import com.gluonhq.strange.ui.QubitFlow;
import com.gluonhq.strange.ui.render.BoardOverlay;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Renderer {

    static {
        Platform.startup(() -> {
            System.err.println("JavaFX Platform initialized");
        });
   }

    public static void renderProgram (Program p) {
        Platform.runLater(() -> showProgram(p));
    }

    public static void showProbabilities(Program p, int count) {
        QuantumExecutionEnvironment simulator = new SimpleQuantumExecutionEnvironment();
        int nq = p.getNumberQubits();
        int[] counter = new int[1 << nq];
        for (int i = 0; i < count; i++) {
            Result result = simulator.runProgram(p);
            int prob = result.getMeasuredProbability();
            counter[prob]++;
           // System.err.println("prob = " + prob);
        }
        for (int i = 0; i < counter.length; i++) {
            System.err.println("cnt ["+i+"]: "+counter[i]);
        }
    }

    public static void showProgram(Program program)  {
        int nQubits = program.getNumberQubits();
        Stage stage = new Stage();
            stage.setTitle("StrangeFX");
            QubitBoard board = new QubitBoard(nQubits);
            List<GateSymbol> multiWires = new LinkedList();
            List<GateSymbol> probabilities = new LinkedList();
            List<BoardOverlay> boardOverlays = new LinkedList<>();
            ObservableList<QubitFlow> wires = board.getWires();
            for (Step s : program.getSteps()) {
                boolean[] gotit = new boolean[nQubits];
                for (Gate gate : s.getGates()) {
                    int qb = gate.getMainQubitIndex();
                    gotit[qb] = true;
                    QubitFlow wire = wires.get(qb);
                    wire.setMinWidth(480);
                    GateSymbol symbol = wire.addGate(gate);
                    if (symbol.spanWires > 1) {
                        if (gate instanceof Oracle) {
                            multiWires.add(symbol);
                            BoardOverlay overlay = new BoardOverlay(s, symbol);
                            boardOverlays.add(overlay);
                            board.addOverlay(overlay);
                        } else {
                            gate.getAffectedQubitIndex().stream().filter(e -> e!= qb).
                                    forEach(a -> {
                                        QubitFlow q = wires.get(a);
                                        GateSymbol symbol2 = q.addAdditonalGateSymbol(gate, 1);
                                        BoardOverlay overlay = new BoardOverlay(s, symbol, symbol2);
                                        boardOverlays.add(overlay);
                                        board.addOverlay(overlay);
                                    });
                        }

                    }
                    if (symbol.probability) {
                        probabilities.add(symbol);
                        BoardOverlay overlay = new BoardOverlay(s, symbol);
                        boardOverlays.add(overlay);
                        board.addOverlay(overlay);
                    }
                }
                for (int i = 0; i < nQubits; i++) {
                    if (!gotit[i]) {
                        QubitFlow wire = wires.get(i);
                        wire.addGate(new Identity());
                    }
                }
            }
            ObservableList<Double> endStates = Model.getInstance().getEndStates();
            Qubit[] qubits = program.getResult().getQubits();
            Complex[] probability = program.getResult().getProbability();
            Double[] endValues = new Double[probability.length];
            int idx = 0;
            for (Qubit qubit: qubits) {
                endValues[idx++] = qubit.getProbability();
            }
            endStates.setAll(endValues);

//            for (GateSymbol symbol : multiWires) {
//                BoardOverlay overlay = new BoardOverlay(symbol);
//                board.addOverlay(overlay);
//            }
            Scene scene = new Scene(board);
            scene.getStylesheets().add(Main.class.getResource("/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        }

//        public void launchMe() {
//            RenderApplication.launch();
//        }
   // }

}
