/*-
 * #%L
 * StrangeFX
 * %%
 * Copyright (C) 2020 Johan Vos
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Johan Vos nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.redfx.strangefx.render;

import org.redfx.strange.Complex;
import org.redfx.strange.Gate;
import org.redfx.strange.Program;
import org.redfx.strange.QuantumExecutionEnvironment;
import org.redfx.strange.Qubit;
import org.redfx.strange.Result;
import org.redfx.strange.Step;
import org.redfx.strange.gate.Identity;
import org.redfx.strange.local.SimpleQuantumExecutionEnvironment;
import org.redfx.strangefx.simulator.RenderModel;
import org.redfx.strangefx.ui.GateSymbol;
import org.redfx.strangefx.ui.Main;
import org.redfx.strangefx.ui.QubitBoard;
import org.redfx.strangefx.ui.QubitFlow;
import org.redfx.strange.ui.render.BoardOverlay;
import javafx.application.Platform;
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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.redfx.strange.gate.X;

public class Renderer {

    private static Stage myStage;
    private RenderModel model;

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
        QuantumExecutionEnvironment simulator = new SimpleQuantumExecutionEnvironment();
        int nq = p.getNumberQubits();
        int[] counter = new int[1 << nq];
        for (int i = 0; i < count; i++) {
            Result result = simulator.runProgram(p);
            int prob = result.getMeasuredProbability();
            counter[prob]++;
        }
        Platform.runLater(() -> renderMeasuredProbabilities(counter));
    }

    public static QubitBoard getRenderGroup(Program program) {
        Renderer renderer = new Renderer();
        return renderer.getQubitBoard(program);
    }

    QubitBoard getQubitBoard(Program program) {
        this.model = new RenderModel(program);
        int nQubits = program.getNumberQubits();
        QubitBoard board = new QubitBoard(model);
        List<GateSymbol> multiWires = new LinkedList();
        List<GateSymbol> probabilities = new LinkedList();
        List<BoardOverlay> boardOverlays = new LinkedList<>();
        ObservableList<QubitFlow> wires = board.getWires();
        List<Step> steps = program.getSteps();
        for (Step s : steps) {
            boolean[] gotit = new boolean[nQubits];
            for (Gate gate : s.getGates()) {
                int qb = gate.getMainQubitIndex();
                gotit[qb] = true;
                QubitFlow wire = wires.get(qb);
                wire.setMinWidth(480);
                GateSymbol symbol = wire.addGate(gate);
            }
            for (int i = 0; i < nQubits; i++) {
                if (!gotit[i]) {
                    QubitFlow wire = wires.get(i);
                    wire.addGate(new Identity());
                }
            }
        }
        Result result = program.getResult();
        
        ObservableList<Double> endStates = model.getEndStates();
        Complex[] probability = result.getProbability();
        Double[] endValues = new Double[probability.length];
        
        Qubit[] qubits = result.getQubits();
        int idx = 0;
        for (Qubit qubit : qubits) {
            endValues[idx++] = qubit.getProbability();
        }
        endStates.setAll(endValues);
        Map<Integer, Qubit[]> intermediateProbabilities = result.getIntermediateQubits();
        model.setIntermediateProbabilities(intermediateProbabilities);
        board.redraw();
        return board;
    }
    
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
        series.setName("occurrences");
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
