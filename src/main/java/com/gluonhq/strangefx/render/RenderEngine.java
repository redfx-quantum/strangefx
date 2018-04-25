package com.gluonhq.strangefx.render;

import com.gluonhq.strange.Gate;
import com.gluonhq.strange.Program;
import com.gluonhq.strange.Step;
import com.gluonhq.strange.gate.X;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author johan
 */
public class RenderEngine {

    /**
     * Create a JavaFX node containing a visualisation of a program
     * @param p
     * @return 
     */
    public static Node createNode(Program p) {
        VBox answer = new VBox(10);
        answer.setTranslateX(50);
        answer.setTranslateY(50);
        int nq = p.getNumberQubits();
        List<Step> steps = p.getSteps();
        // render all qubits, store in Pane[]
        Pane[] pane = new Pane[nq];
        for (int i = 0; i < nq; i++) {
            // for each qubit...
            pane[i] = new Pane();
            Line l = new Line(0, 0, 200, 0);
            l.setStrokeWidth(3);
            l.setStroke(Color.BLUE);
            Circle qubit = new Circle(20, Color.RED);
            
            pane[i].getChildren().addAll(l, qubit);

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
                g.setTranslateX(100 * (idx + 1));
                g.setTranslateY(-15);
                pane[mqi].getChildren().add(g);
                idx++;
            }
        }
        return answer;
    }

}