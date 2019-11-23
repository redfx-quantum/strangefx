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
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Renderer {

    private static Stage myStage;

    static {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                System.err.println("JavaFX Platform initialized");
            }
        };
        try {
            Platform.startup(r);
        } catch (java.lang.IllegalStateException e) {
            System.err.println("Toolkit already initialized, ignore");
        }
    }

    public static void renderProgram(Program p) {
        Platform.runLater(() -> showProgram(p));
    }

    public static void showProbabilities(Program p, int count) {
        System.err.println("SHOWPROB, count = " + count);
        QuantumExecutionEnvironment simulator = new SimpleQuantumExecutionEnvironment();
        int nq = p.getNumberQubits();
        int[] counter = new int[1 << nq];
        for (int i = 0; i < count; i++) {
            Result result = simulator.runProgram(p);
            int prob = result.getMeasuredProbability();
            counter[prob]++;
        }
        for (int i = 0; i < counter.length; i++) {
            System.err.println("cnt [" + i + "]: " + counter[i]);
        }
        Platform.runLater(() -> renderMeasuredProbabilities(counter));
    }

    public static QubitBoard getRenderGroup(Program program) {
        int nQubits = program.getNumberQubits();

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
                        gate.getAffectedQubitIndex().stream().filter(e -> e != qb).
                                forEach(a -> {
                                    QubitFlow q = wires.get(a);
                                    GateSymbol symbol2 = q.addAdditonalGateSymbol(gate, 1);
                                    gotit[a] = true;
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
        for (Qubit qubit : qubits) {
            endValues[idx++] = qubit.getProbability();
        }
        endStates.setAll(endValues);
        return board;
    }

    /**
     * Release resources associated with this particular QubitBoard
     * @param board
     */
    public static void disable(QubitBoard board) {
        board.getWires().stream().forEach(flow -> flow.cleanup());
    }

    public static void showProgram(Program program) {
        Stage stage = new Stage();
        myStage = stage;
        stage.setTitle("StrangeFX");
        Group board = getRenderGroup(program);
        VBox vbox = new VBox(40);
        vbox.getChildren().add(board);
        Scene scene = new Scene(vbox);
        scene.getStylesheets().add(Main.class.getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void renderMeasuredProbabilities(int[] results) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Integer> barChart = new BarChart(xAxis, yAxis);
        barChart.setData(getChartData(results));
        barChart.setTitle("Measured probability distribution");
        StackPane root = new StackPane();
        root.getChildren().add(barChart);
        if (myStage != null) {
            Scene oldscene = myStage.getScene();
            VBox box = (VBox)(oldscene.getRoot());
            oldscene.setRoot(new StackPane());
            box.getChildren().add(root);
            Scene newScene = new Scene(box);
            newScene.getStylesheets().add(Main.class.getResource("/styles.css").toExternalForm());
            myStage.setScene(newScene);
        } else {
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 640, 480));
            stage.show();
        }

    }

    private static ObservableList<XYChart.Series<String, Integer>> getChartData(int[] results) {
        ObservableList<XYChart.Series<String, Integer>> answer = FXCollections.observableArrayList();
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("occurences");
        answer.add(series);
        for (int i = 0; i < results.length; i++) {
            series.getData().add(new XYChart.Data<>(getFixedBinaryString(i, (int) (Math.log(results.length) / Math.log(2))), results[i]));
        }
        return answer;
    }

    private static String getFixedBinaryString(int i, int w) {
        StringBuffer buff = new StringBuffer(Integer.toBinaryString(i));
        while (buff.length() < w) {
            buff.insert(0, "0");
        }
        return buff.toString();
    }


}
