package com.gluonhq.strangefx.render;

import com.gluonhq.strange.*;
import com.gluonhq.strange.gate.X;
import com.gluonhq.strange.ui.*;
import javafx.application.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.stage.*;

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
            QubitBoard board = new QubitBoard(nQubits);
            System.out.println("START called on stage, board = "+board+", nQubits = "+ nQubits);
            ObservableList<QubitFlow> qubits = board.getQubits();
            for (Step s : program.getSteps()) {
                for (Gate gate : s.getGates()) {
                    int qb = gate.getMainQubitIndex();
                    QubitFlow wire = qubits.get(qb);
                    wire.addGate(gate);
                }
            }
//            for (int i = 0; i < nQubits; i++) {
//                QubitFlow wire = qubits.get(i);
//                wire.addGate(new X(i));
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
