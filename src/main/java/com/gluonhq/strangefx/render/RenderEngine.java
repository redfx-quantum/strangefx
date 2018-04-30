package com.gluonhq.strangefx.render;

import com.gluonhq.strange.Gate;
import com.gluonhq.strange.Program;
import com.gluonhq.strange.Step;
import com.gluonhq.strange.gate.X;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javafx.animation.TranslateTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
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
    }
    /**
     * Create a JavaFX node containing a visualisation of a program
     * @param p
     * @return 
     */
    public static RenderEngine createNode(Program p) {
        RenderEngine answer = new RenderEngine();
        answer.setTranslateX(50);
        answer.setTranslateY(50);
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
            l.setStrokeWidth(3);
            l.setStroke(Color.BLUE);
            Qubit3D qubit3D = new Qubit3D();

            pane[i].getChildren().addAll(qubit3D, l);
            qubit3D.translateXProperty().addListener((Observable o) -> {
                int mystep = (int)(qubit3D.getTranslateX()/stepSize);
                int cstep = qubit3D.getCurrentStep();
                if (mystep > cstep) {
                    qubit3D.incrementStep();
                    qubit3D.flip();
                }
            });
            // TODO: set rod rotation
            qubit3D.rotateRod(new Rotate(new Random().nextInt(360), 0, 0, 0, Rotate.Z_AXIS));
            answer.qubits.add(qubit3D);
            answer.getChildren().add(pane[i]);
        }
        for (Step step : steps) {
            List<Gate> gates = step.getGates();
            int idx = 0;
            for (Gate gate : gates) {
                int mqi = gate.getMainQubitIndex();
                Group g = new Group();
                Rectangle gateui = new Rectangle(40, 40, Color.YELLOW);
                Label l = new Label("?");
                if (gate instanceof X) {
                    l.setText("X");
                }
                g.getChildren().addAll(gateui, l);
                g.setTranslateX(-200 + stepSize * (idx + 1));
                pane[mqi].getChildren().add(g);
                idx++;
            }
        }
        return answer;
    }
    
    public void animate() {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(5), qubits.get(0));
        tt.setByX(nSteps * stepSize);
        tt.play();
    }

}