package com.gluonhq.strangefx.render;

import com.gluonhq.strange.*;
import com.gluonhq.strange.gate.Identity;
import com.gluonhq.strange.gate.Oracle;
import com.gluonhq.strange.gate.X;
import com.gluonhq.strange.simulator.Model;
import com.gluonhq.strange.ui.*;
import com.gluonhq.strange.ui.render.*;
import javafx.application.*;
import javafx.beans.*;
import javafx.beans.Observable;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.stage.*;

import java.util.*;

public class Renderer {

    private RenderApplication application;

    public static void renderProgram (Program p) {
        Renderer renderer = new Renderer(p);
        renderer.show();
    }

    public Renderer(Program p) {
        int nq = p.getNumberQubits();
        System.err.println("Renderer created nQubits = "+nq);
        this.application = new RenderApplication(nq, p);
    }

    public void show() {
        Application.launch(RenderApplication.class);
    }



    public static class RenderApplication extends Application {

        private static int nQubits;
        private static QubitBoard board;
        private static Program program;

        public RenderApplication() {}

        public RenderApplication(int nq, Program p) {
            this.nQubits = nq;
            this.program = p;
        }

        public void setBoard(QubitBoard qb) {
            this.board = qb;
        }

        @Override
        public void start(Stage stage) throws Exception {
            stage.setTitle("StrangeFX");
            QubitBoard board = new QubitBoard(nQubits);
            System.out.println("START called on stage, board = "+board+", nQubits = "+ nQubits);
            List<GateSymbol> multiWires = new LinkedList();
            List<GateSymbol> probabilities = new LinkedList();
            List<BoardOverlay> boardOverlays = new LinkedList<>();
            ObservableList<QubitFlow> wires = board.getWires();
            for (Step s : program.getSteps()) {
                System.err.println("add step "+s);
                boolean[] gotit = new boolean[nQubits];
                for (Gate gate : s.getGates()) {
                    System.err.println("Add gate "+gate);
                    int qb = gate.getMainQubitIndex();
                    gotit[qb] = true;
                    QubitFlow wire = wires.get(qb);
                    GateSymbol symbol = wire.addGate(gate);
                    System.err.println("Add symbol "+symbol +" to wire "+wire);
                    if (symbol.spanWires > 1) {
                        System.err.println("More than 1 gate");
                        if (gate instanceof Oracle) {
                            multiWires.add(symbol);
                            BoardOverlay overlay = new BoardOverlay(s, symbol);
                            boardOverlays.add(overlay);
                            board.addOverlay(overlay);
                        } else {
                            gate.getAffectedQubitIndex().stream().filter(e -> e!= qb).
                                    forEach(a -> {
                                        QubitFlow q = wires.get(a);
                                        q.addAdditonalGateSymbol(gate, 1);
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
            Qubit[] qubits = this.program.getResult().getQubits();
            Complex[] probability = this.program.getResult().getProbability();
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

        public void launchMe() {
            RenderApplication.launch();
        }
    }

}
