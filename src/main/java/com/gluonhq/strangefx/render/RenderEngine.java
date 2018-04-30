package com.gluonhq.strangefx.render;

import com.gluonhq.strange.Gate;
import com.gluonhq.strange.Program;
import com.gluonhq.strange.Step;
import com.gluonhq.strange.gate.X;
import java.util.List;
import java.util.Random;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

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

            // TODO: set rod rotation
            qubit3D.rotateRod(new Rotate(new Random().nextInt(360), 0, 0, 0, Rotate.Z_AXIS));
            
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
                g.setTranslateX(-200 + 100 * (idx + 1));
                pane[mqi].getChildren().add(g);
                idx++;
            }
        }
        return answer;
    }

}