package com.gluonhq.strange.ui;

import com.gluonhq.strange.simulator.Gate;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

import java.util.Objects;

public class GateSymbol extends Label {

    static final DataFormat DRAGGABLE_GATE = new DataFormat("draggable-gate");

    private final Gate gate;
    private final boolean movable;

    public static GateSymbol of( Gate gate, Boolean movable ) {
        return new GateSymbol(gate, movable);
    }

    public static GateSymbol of( Gate gate ) {
        return new GateSymbol(gate, true);
    }
    
    private GateSymbol( Gate gate, boolean movable ) {
        getStyleClass().setAll("gate-symbol");
        this.gate = Objects.requireNonNull(gate);
        this.movable = movable;
        setText(gate.getCaption());
        setMinWidth(40);
        setAlignment(Pos.CENTER);
        setStyle("-fx-border-color: rootColor; -fx-padding: 10 5 10 5; -fx-text-fill: rootColor");

        if (movable) {
            setContextMenu(buildContextMenu());
        }

        setOnDragDetected(e -> {

            System.getProperties().put(DRAGGABLE_GATE, this);

            Dragboard db = this.startDragAndDrop(  isMovable()? TransferMode.MOVE: TransferMode.COPY);
            db.setDragView(this.snapshot(null, null));
            ClipboardContent content = new ClipboardContent();
            content.putString(gate.getCaption());
            content.put(DRAGGABLE_GATE, "");
            db.setContent(content);
            e.consume();
        });
        
        this.setOnDragDone(e -> {
            System.out.println("Drag Done");
        });
        
        this.setOnDragDropped(e -> {
            System.out.println("Drag dropped");
        });

    }

    public Gate getGate() {
        return gate;
    }

    public boolean isMovable() {
        return movable;
    }

    private ContextMenu buildContextMenu() {

        ContextMenu menu = new ContextMenu();

        MenuItem mnRemove = new MenuItem("Remove");
        menu.setOnAction( e -> removeFromParent());

        menu.getItems().addAll(mnRemove);
        return menu;
    }

    private void removeFromParent() {
        Pane parent = (Pane) getParent();
        if (parent != null) {
            parent.getChildren().remove(this);
        }
    }
}
