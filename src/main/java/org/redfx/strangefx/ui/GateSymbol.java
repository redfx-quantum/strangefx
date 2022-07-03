/*-
 * #%L
 * StrangeFX
 * %%
 * Copyright (C) 2020, 2021 Johan Vos
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
package org.redfx.strangefx.ui;

import java.util.Arrays;
import java.util.List;
import org.redfx.strange.gate.*;
import org.redfx.strange.gate.ProbabilitiesGate;
import org.redfx.strange.Gate;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.Objects;
import javafx.scene.shape.Rectangle;
import org.redfx.strange.BlockGate;
import org.redfx.strange.Complex;

public class GateSymbol extends Label {

    static final DataFormat DRAGGABLE_GATE = new DataFormat("draggable-gate");

    public static final int HEIGHT = 40;
    public static final int WIDTH = 40;
    public static final int SEP = QubitBoard.WIRE_HEIGHT;
    private final Gate gate;
    private final boolean movable;
    public int spanWires = 1;
    public boolean probability = false;

    private QubitFlow wire;
    
    private int index;

    /**
     * Create a new GateSymbol instance, and a new instance of the provided Gate class.No parameters are set on this gate.
     * @param clazz the class of the Gate
     * @param idx the index this Gate occupies
     * @return the created GateSymbol
     */
    public static GateSymbol of( Class<? extends Gate> clazz, int idx) {
        return GateSymbol.of(clazz, idx, true);
    }
    
    /**
     * Create a new GateSymbol instance, and a new instance of the provided Gate class.
     * No parameters are set on this gate.
     * @param clazz
     * @param movable
     * @return 
     */
    public static GateSymbol of( Class<? extends Gate> clazz, int idx, Boolean movable ) {
        GateSymbol answer = null;
        try {
            answer = new GateSymbol(clazz.getDeclaredConstructor(int.class).newInstance(idx), movable, 0);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Can't create gatesymbol for class "+clazz, ex);
        }
        return answer;
    }
        
    public static GateSymbol of( Gate gate, Boolean movable ) {
        return new GateSymbol(gate, movable, 0);
    }

    public static GateSymbol of( Gate gate ) {
        return new GateSymbol(gate, true, 0);
    }

    public static GateSymbol of( Gate gate, int idx ) {
        return new GateSymbol(gate, true, idx);
    }
    
    public static GateSymbol of (ControlQubit ctrl) {
        return new GateSymbol(ctrl);
    }
    
    public enum ControlQubit {
        ON,
        OFF
    }

    GateSymbol( Gate gate, boolean movable) {
        this (gate, movable, 0);
    }
    
    GateSymbol( Gate gate, boolean movable, int idx) {
        this.index = idx;
        this.spanWires = gate.getAffectedQubitIndexes().size();
        this.gate = Objects.requireNonNull(gate);
        this.movable = movable;
        if (!(gate instanceof Identity)) {
            if (gate instanceof BlockGate) {
                setGraphic(createBlockNode( (BlockGate) gate));
            } else if (gate instanceof Oracle) {
                setGraphic(createOracleNode( (Oracle) gate));
            } else if (gate instanceof Cnot) {
                setGraphic(createCNotNode((Cnot)gate));
            } else if (gate instanceof Toffoli) {
                setGraphic(createToffoliNode((Toffoli)gate));
            } else if (gate instanceof ProbabilitiesGate) {
                setGraphic(createProbabilitiesNode((ProbabilitiesGate)gate));
            } 
            else if (idx == 0 && gate instanceof Cz) {
                setGraphic(createCZNode((Cz)gate));
            } else {
                getStyleClass().setAll("gate-symbol", getStyle(gate.getGroup()));
                setText(gate.getCaption());
            }
        }
        setMinWidth(WIDTH);
        setMinHeight(HEIGHT);
        setAlignment(Pos.CENTER);
        if (gate instanceof Oracle) {
            Oracle oracle = (Oracle)gate;
            this.spanWires = oracle.getQubits();
            this.setOpacity(0.9);
        }
        if (gate instanceof ProbabilitiesGate) {
            this.probability = true;
        }
        if (movable) {
            setContextMenu(buildContextMenu());
        }
        prepareDrag();
    }

    void prepareDrag() {
          setOnDragDetected(e -> {
            System.getProperties().put(DRAGGABLE_GATE, this);

          //  Dragboard db = this.startDragAndDrop(  isMovable()? TransferMode.MOVE: TransferMode.COPY);
            Dragboard db = this.startDragAndDrop(TransferMode.ANY);
            db.setDragView(this.snapshot(null, null));
            ClipboardContent content = new ClipboardContent();
            content.putString(getName());
            content.put(DRAGGABLE_GATE, "");
            db.setContent(content);
            e.consume();
        });
        this.setOnDragDone(e -> {
            // clear out the ref to the dragged node
            System.getProperties().remove(DRAGGABLE_GATE);
        });
        
        this.setOnDragDropped(e -> {
        });
    }
    
    GateSymbol(ControlQubit ctrl) {
        this.gate = null;
        this.movable = false;
        setDot();
        prepareDrag();
    }
    
    public void setWire(QubitFlow f) {
        this.wire = f;
    }
    
    public String getName() {
        return (gate == null ? "NOT" : gate.getName());
    }
    
    public boolean isIdentity() {
        return (this.gate instanceof Identity);
    }
    
    private String getStyle(String group ) {
        return group.toLowerCase().replaceAll("_", "");
    }

    public Gate getGate() {
        return gate;
    }

    public boolean isMovable() {
        return movable;
    }

    private ContextMenu buildContextMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem mnRemove = new MenuItem("Remove");//, MaterialIconFactory.get().createIcon(MaterialIcon.DELETE));
        menu.setOnAction( e -> removeFromParent());
        menu.getItems().addAll(mnRemove);
        return menu;
    }

    public void removeFromParent() {
        Pane parent = (Pane) getParent();
        if (parent != null) {
            parent.getChildren().remove(this);
        }
        if (this.wire != null) {
            this.wire.gateSymbolRemoved(this);
        }
    }

    private Parent createProbabilitiesNode(ProbabilitiesGate gate) {
        AnchorPane anchor = new AnchorPane();
        Complex[] ip = gate.getProbabilities();
        int N = ip.length;
        int nq = (int) (Math.log(N)/Math.log(2));
        double deltaY = (66. * nq - 10 + 38) / N;
        Group answer = new Group();

        Rectangle rect2 = new Rectangle(0, 0, 40, 66 * nq - 10 + 38);
        rect2.setFill(Color.WHITE);
        rect2.setStroke(Color.BLUE);
        rect2.setStrokeWidth(1);
        answer.getChildren().add(rect2);

        for (int i = 0; i < N; i++) {
            double startY = i * deltaY;
            Rectangle minibar = new Rectangle(1, i * deltaY, 38 * ip[i].abssqr(), deltaY - 1);
            minibar.setFill(Color.GREEN);
            Line l = new Line(1, startY, 39, startY);
            l.setFill(Color.LIGHTGRAY);
            l.setStrokeWidth(1);
            answer.getChildren().add(l);
            answer.getChildren().add(minibar);
        }
        
        anchor.getChildren().add(answer);
        return anchor;
    }

    private Parent createOracleNode(Oracle gate) {
        AnchorPane answer = new AnchorPane();
        Label l = new Label(gate.getCaption());
        l.setTranslateX(2);
        l.setTranslateY(2);
        l.getStyleClass().setAll("gate-block-text");
        List<Integer> qidxs = gate.getAffectedQubitIndexes();
        int mqi = gate.getMainQubitIndex();
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int qidx : qidxs) {
            if (qidx > max) {
                max = qidx;
            }
            if (qidx < min) {
                min = qidx;
            }
        }
        int span = max - min + 1;
        Rectangle rect = new Rectangle(0, 0, HEIGHT, (HEIGHT) + (span - 1) * SEP);
        rect.setFill(Color.YELLOWGREEN);
        rect.setStroke(Color.GREEN);
        rect.setStrokeWidth(2);
        if (min < mqi) {
            // block doesn't start at main qubit, translate to start
            rect.setLayoutY(SEP * (min - mqi));
        }
        answer.getChildren().add(rect);
        answer.getChildren().add(l);
        return answer;
    }

    private Parent createBlockNode(BlockGate gate) {
        AnchorPane answer = new AnchorPane();
//        answer.getStyleClass().setAll("gate-block-text");
        Label l = new Label(gate.getCaption());
        l.setTranslateX(2);
        l.setTranslateY(2);
        l.getStyleClass().setAll("gate-block-text");
        List<Integer> qidxs = gate.getAffectedQubitIndexes();
        int mqi = gate.getMainQubitIndex();
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int qidx : qidxs) {
            if (qidx > max) max = qidx;
            if (qidx < min) min = qidx;
        }
        int span = max - min+1;
        Rectangle rect = new Rectangle(0,0, HEIGHT, (HEIGHT) +(span-1) * SEP );
        rect.setFill(Color.LIGHTGREEN);
        if (min < mqi) {
            // block doesn't start at main qubit, translate to start
            rect.setLayoutY(SEP*(min-mqi));
        }
        answer.getChildren().add(rect);
        answer.getChildren().add(l);
        return answer;
    }

    static double LINE_WIDTH = 2;
    private Parent createCNotNode(Cnot cnot) {
        double div2 =  HEIGHT/2;
        int midx = cnot.getMainQubitIndex();
        int sidx = cnot.getSecondQubitIndex();
        AnchorPane answer = new AnchorPane();
        Circle con = new Circle(0, 0, 5, Color.DARKGREY);
        con.setTranslateY(-5);
        Circle c = new Circle(0, 0, 10, Color.TRANSPARENT);
        c.setTranslateY(-10);
        c.setLayoutY(SEP * (sidx - midx));
        c.setStroke(Color.DARKGRAY);
        c.setStrokeWidth(2);
        Line l = new Line(0,0, 0, SEP* Math.abs(sidx-midx) + 10);
        if (midx > sidx) {
            l.setTranslateY(-l.getEndY());
        }
        l.setStrokeWidth(LINE_WIDTH);
        l.setStroke(Color.DARKGRAY);
        AnchorPane.setTopAnchor(con, (double)HEIGHT/2);
        AnchorPane.setTopAnchor(l, (double)HEIGHT/2);
        AnchorPane.setTopAnchor(c, (double)(SEP * (sidx-midx)+HEIGHT/2));
        AnchorPane.setLeftAnchor(con, div2-5);
        AnchorPane.setLeftAnchor(l, div2-LINE_WIDTH/2);
        AnchorPane.setLeftAnchor(c, div2-10);
        answer.getChildren().addAll(con, c, l);
        answer.setPrefWidth(40);
        return answer;
    }
    private Parent createCZNode(Cz cnot) {
        double div2 =  HEIGHT/2;
        int midx = cnot.getMainQubitIndex();
        int sidx = cnot.getSecondQubitIndex();
        AnchorPane answer = new AnchorPane();
        Circle con = new Circle(0, 0, 5, Color.DARKGREY);
        con.setTranslateY(-5);
        Label z = new Label("Z");
        z.getStyleClass().setAll("gate-symbol", getStyle(gate.getGroup()));
        z.setTranslateY(-HEIGHT/2);
        z.setLayoutY(SEP * (sidx - midx));
        z.setMinWidth(WIDTH);
        z.setMinHeight(HEIGHT);
        z.setAlignment(Pos.CENTER);
        Line l = new Line(0,0, 0, SEP* (sidx-midx) - HEIGHT/2);
        l.setStrokeWidth(LINE_WIDTH);
        l.setStroke(Color.DARKGRAY);
        AnchorPane.setTopAnchor(con, (double)HEIGHT/2);
        AnchorPane.setTopAnchor(l, (double)HEIGHT/2);
        AnchorPane.setTopAnchor(z, (double)(SEP * (sidx-midx)+HEIGHT/2));
        AnchorPane.setLeftAnchor(con, div2-5);
        AnchorPane.setLeftAnchor(l, div2-LINE_WIDTH/2);
        AnchorPane.setLeftAnchor(z, div2-HEIGHT/2);
        answer.getChildren().addAll(con, z, l);
        answer.setPrefWidth(40);
        return answer;
    }

    private Parent createToffoliNode(Toffoli toffoli) {
        double div2 =  HEIGHT/2;
        int midx = toffoli.getMainQubitIndex();
        int idx2 = toffoli.getSecondQubit();
        int idx3 = toffoli.getThirdQubit();
        AnchorPane answer = new AnchorPane();
        Circle con = new Circle(0, 0, 5, Color.DARKGREY);
        con.setTranslateY(-5);

        Circle con2 = new Circle(0, 0, 5, Color.DARKGRAY);
        con2.setTranslateY(-5);

        Circle c = new Circle(0, 0, 10, Color.TRANSPARENT);
        c.setTranslateY(-10);

        c.setStroke(Color.DARKGRAY);
        c.setStrokeWidth(2);
        Line l = new Line(0,0, 0, SEP * (idx3-midx) + 10);
        l.setStrokeWidth(LINE_WIDTH);
        l.setStroke(Color.DARKGRAY);

        AnchorPane.setTopAnchor(con, (double)HEIGHT/2);
        AnchorPane.setTopAnchor(l, (double)HEIGHT/2);
        AnchorPane.setTopAnchor(c, (double)(SEP * (idx3-midx)+HEIGHT/2));
        AnchorPane.setTopAnchor(con2, (double)(SEP * (idx2-midx)+HEIGHT/2));
        AnchorPane.setLeftAnchor(con2, div2-5);
        AnchorPane.setLeftAnchor(con, div2-5);
        AnchorPane.setLeftAnchor(l, div2-LINE_WIDTH/2);
        AnchorPane.setLeftAnchor(c, div2-10);
        answer.getChildren().addAll(con, con2, c, l);
        answer.setPrefWidth(40);
        return answer;
    }

    void setDot() {
        setGraphic(createDotGroup());
        setText("");
    }

    Group createDotGroup() {
        Group g = new Group();
        Circle c = new Circle(0, 0, 5, Color.DARKGREY);
        g.getChildren().add(c);
        setContentDisplay(ContentDisplay.CENTER);
        return g;
    }

    @Override 
    public String toString() {
        return "GateSymbol for gate "+gate+" add address "+super.toString();
    }
}
