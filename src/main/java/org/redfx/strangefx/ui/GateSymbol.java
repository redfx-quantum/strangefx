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
package org.redfx.strangefx.ui;

import java.util.List;
import org.redfx.strange.gate.*;
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

public class GateSymbol extends Label {

    static final DataFormat DRAGGABLE_GATE = new DataFormat("draggable-gate");

    public static final int HEIGHT = 40;
    public static final int SEP = 77;
    private final Gate gate;
    private final boolean movable;
    public int spanWires = 1;
    public boolean probability = false;

    private int index = -1;
    private QubitFlow wire;
    
    /**
     * Create a new GateSymbol instance, and a new instance of the provided Gate class.
     * No parameters are set on this gate.
     * @param clazz
     * @return 
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
            } else if (gate instanceof Cnot) {
                setGraphic(createCNotNode((Cnot)gate));
            } else if (gate instanceof Toffoli) {
                if (idx == 0 || idx == 1) {
                    // first symbol of Cnot is dot
                    setDot();
                } else {
                    Group g = new Group();
                    Circle c = new Circle(0, 0, 10, Color.TRANSPARENT);
                    c.setStroke(Color.DARKGRAY);
                    c.setStrokeWidth(2);
                    Line l = new Line(0, -10, 0, 10);
                    l.setStrokeWidth(2);
                    l.setStroke(Color.DARKGRAY);
                    g.getChildren().addAll(c, l);
                    setContentDisplay(ContentDisplay.CENTER);
                    setGraphic(g);
                    setText(""); 
                }
            } 
            else if (idx == 0 && gate instanceof Cz) {
                setDot();
            } else {
                getStyleClass().setAll("gate-symbol", getStyle(gate.getGroup()));
                setText(gate.getCaption());
            }
        }
        setMinWidth(40);
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

    private Parent createBlockNode(BlockGate gate) {
        AnchorPane answer = new AnchorPane();
        List<Integer> qidxs = gate.getAffectedQubitIndexes();
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int qidx : qidxs) {
            if (qidx > max) max = qidx;
            if (qidx < min) min = qidx;
        }
        int span = max - min+1;
        Rectangle rect = new Rectangle(0,0, HEIGHT, (HEIGHT) +(span-1) * SEP );
        rect.setFill(Color.LIGHTGREEN);
        answer.getChildren().add(rect);
        return answer;
    }

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
        Line l = new Line(0,0, 0, SEP* (sidx-midx) + 10);
        l.setStrokeWidth(2);
        l.setStroke(Color.DARKGRAY);
        AnchorPane.setTopAnchor(con, (double)HEIGHT/2);
        AnchorPane.setTopAnchor(l, (double)HEIGHT/2);
        AnchorPane.setTopAnchor(c, (double)(SEP * (sidx-midx)+HEIGHT/2));
        AnchorPane.setLeftAnchor(con, div2-5);
        AnchorPane.setLeftAnchor(l, div2);
        AnchorPane.setLeftAnchor(c, div2-10);
        answer.getChildren().addAll(con, c, l);
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
