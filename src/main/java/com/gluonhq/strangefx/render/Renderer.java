package com.gluonhq.strangefx.render;

import com.gluonhq.strange.*;
import com.gluonhq.strange.gate.X;
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
            ObservableList<QubitFlow> wires = board.getWires();
            for (Step s : program.getSteps()) {
                for (Gate gate : s.getGates()) {
                    int qb = gate.getMainQubitIndex();
                    QubitFlow wire = wires.get(qb);
                    GateSymbol symbol = wire.addGate(gate);
                    if (symbol.spanWires > 1) {
                        System.err.println("More than 1 gate");
                        multiWires.add(symbol);
                    }
                }
            }

            for (GateSymbol symbol : multiWires) {
                BoardOverlay overlay = new BoardOverlay(symbol);
                board.addOverlay(overlay);
//                symbol.boundsInParentProperty().addListener(new InvalidationListener() {
//                    @Override
//                    public void invalidated(Observable observable) {
//                        Region region = new Region();
//                        Bounds parentBounds = symbol.getBoundsInParent();
//                        System.err.println("visible? "+ symbol.isVisible()+", bounds = "+parentBounds);
//                    }
//                });

            }
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
