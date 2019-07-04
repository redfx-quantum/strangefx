package com.gluonhq.strange.ui.render;

import com.gluonhq.strange.*;
import com.gluonhq.strange.ui.*;
import javafx.beans.*;
import javafx.geometry.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

public class BoardOverlay extends Region {

    private GateSymbol symbol;

    public BoardOverlay(Step s, GateSymbol symbol) {
        System.err.println("Create BO, s = "+s);
        this.symbol = symbol;
        if (symbol.probability) {
            createProbability(s, symbol);
        } else {
            createOracle(symbol);
        }
    }

    public BoardOverlay(Step s, GateSymbol symbol1, GateSymbol symbol2) {
        this.symbol = symbol1;
        this.symbol.boundsInParentProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                BoardOverlay me = BoardOverlay.this;
                me.getChildren().clear();
                Bounds bp = symbol.getBoundsInParent();
                Point2D beginPoint = symbol.localToScene(symbol.getWidth()/2, symbol.getHeight()/2);

                Point2D endPoint = symbol2.localToScene(symbol2.getWidth()/2, symbol2.getHeight()/2);
                Line line = new Line(beginPoint.getX(), beginPoint.getY(), endPoint.getX(), endPoint.getY());

                line.setStroke(Color.BLUE);
                line.setStrokeWidth(1);
                BoardOverlay.this.getChildren().add(line);
            }
        });
    }

    private void createProbability(Step s, GateSymbol symbol) {
        Gate gate = symbol.getGate();
        System.err.println("Prob for step "+s.getIndex());
        Program program = s.getProgram();
        Result result = program.getResult();
        Complex[] ip = result.getIntermediateProbability(s.getIndex());
        System.err.println(" = "+ip[3].abssqr());
        int nq = program.getNumberQubits();
        int N = 1<<nq;
        double deltaY = (66.*nq-10+38)/N;
        System.err.println("n = "+nq+" and N = "+N+", dY = "+deltaY);
        this.symbol.boundsInParentProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                BoardOverlay me = BoardOverlay.this;
                me.getChildren().clear();
                Bounds bp = symbol.getBoundsInParent();
                Point2D base = symbol.localToScene(0, 0);
                Rectangle rect2 = new Rectangle(base.getX() , base.getY(),40, 66 * nq-10+38);
                rect2.setFill(Color.WHITE);
                rect2.setStroke(Color.BLUE);
                rect2.setStrokeWidth(1);
                BoardOverlay.this.getChildren().add(rect2);

                for (int i = 0; i < N; i++) {
                    double startY = base.getY()+ i * deltaY;
                    Rectangle minibar = new Rectangle(1+base.getX(), base.getY()+ i*deltaY, 38 * ip[i].abssqr(), deltaY-1);
                    minibar.setFill(Color.GREEN);
                    Line l = new Line(1+base.getX(),startY,base.getX()+39,startY);
                    l.setFill(Color.LIGHTGRAY);
                    l.setStrokeWidth(1);
                    BoardOverlay.this.getChildren().add(l);
                    BoardOverlay.this.getChildren().add(minibar);
                }

            }
        });
    }
    private void createOracle(GateSymbol symbol) {
        this.symbol.boundsInParentProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                BoardOverlay me = BoardOverlay.this;
                me.getChildren().clear();
                Bounds bp = symbol.getBoundsInParent();
                Point2D base = symbol.localToScene(0, 0);
//                System.out.println("bp = "+bp+" and p2d = "+base);
                Rectangle rect = new Rectangle(base.getX() ,38 + base.getY(),40, 66 * symbol.spanWires-10);
                Rectangle rect2 = new Rectangle(base.getX() , base.getY(),40, 66 * symbol.spanWires-10+38);

//                 Rectangle rect = new Rectangle(base.getX() + bp.getMinX(),base.getY()+bp.getMinY(),40, 40 * symbol.spanWires);
                rect.setFill(Color.YELLOWGREEN);
                rect2.setStroke(Color.GREEN);
                rect2.setStrokeWidth(2);
                rect2.setFill(Color.TRANSPARENT);
                BoardOverlay.this.getChildren().setAll(rect, rect2);
//                System.out.println("rect at "+rect);
            }
        });
    }



}
