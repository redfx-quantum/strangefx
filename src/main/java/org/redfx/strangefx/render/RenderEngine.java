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

import org.redfx.strange.Gate;
import org.redfx.strange.Program;
import org.redfx.strange.Step;
import org.redfx.strangefx.ui.GateSymbol;
import org.redfx.strangefx.ui.Main;
import java.util.LinkedList;
import java.util.List;
import javafx.animation.TranslateTransition;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author johan
 */
public class RenderEngine extends VBox {

    //the size between 2 steps
    static final int stepSize = 200;
    int nSteps;
    
    private List<Qubit3D> qubits = new LinkedList<>();
    public RenderEngine() {
        super(50);
        setPadding(new Insets(50));
        getStyleClass().add("qubit");
    }
    /**
     * Create a JavaFX node containing a visualisation of a program
     * @param p
     * @return 
     */
    public static RenderEngine createNode(Program p) {
        RenderEngine answer = new RenderEngine();
        int nq = p.getNumberQubits();
        List<Step> steps = p.getSteps();
        answer.nSteps = steps.size();
        // render all qubits, store in Pane[]
        HBox[] pane = new HBox[nq];
        for (int i = 0; i < nq; i++) {
            // for each qubit...
            pane[i] = new HBox();
            pane[i].setAlignment(Pos.CENTER_LEFT);
            Line l = new Line(0, 0, 200, 0);
            l.getStyleClass().add("wire");
            Qubit3D qubit3D = new Qubit3D();

            pane[i].getChildren().addAll(qubit3D, l);
            qubit3D.translateXProperty().addListener((Observable o) -> {
                int mystep = (int)((qubit3D.getTranslateX()-1)/stepSize);
                int cstep = qubit3D.getCurrentStep();
                if (mystep > cstep) {
                    qubit3D.incrementStep();
                    qubit3D.flip();
                }
            });
            answer.qubits.add(qubit3D);
            answer.getChildren().add(pane[i]);
        }
        for (Step step : steps) {
            List<Gate> gates = step.getGates();
            int idx = 0;
            for (Gate gate : gates) {
                int mqi = gate.getMainQubitIndex();
                GateSymbol symbol = GateSymbol.of(gate, false);
                symbol.setTranslateX(-200 + stepSize * (idx + 1));
                pane[mqi].getChildren().add(symbol);
                idx++;
            }
        }
        return answer;
    }
    
    private static int counter = 0;
    
    public void animate() {
        final Qubit3D node = qubits.get(counter++ % qubits.size());
        TranslateTransition tt = new TranslateTransition(Duration.seconds(5), node);
        node.resetStep();
        tt.setFromX(0);
        tt.setByX(nSteps * stepSize);
        tt.playFromStart();
    }
    
    public static void showCircuit(Program p) {
        System.out.println("will show circuit for "+p);
        RenderEngine re = createNode(p);
        System.out.println("re = "+re);
         
        Scene scene = new Scene(re, 800, 600);
        scene.getStylesheets().add(Main.class.getResource("/styles.css").toExternalForm());
                
        Stage stage = new Stage();
        stage.setTitle("StrangeFX rendering");
        stage.setScene(scene);
        System.out.println("show stage...");
        stage.show();
        System.out.println("showed scene");

    }

}
