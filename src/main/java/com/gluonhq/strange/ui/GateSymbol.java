package com.gluonhq.strange.ui;

import com.gluonhq.strange.simulator.Gate;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.utils.MaterialIconFactory;
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
            // clear out the ref to the dragged node
            System.getProperties().remove(DRAGGABLE_GATE);
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

        MenuItem mnRemove = new MenuItem("Remove", MaterialIconFactory.get().createIcon(MaterialIcon.DELETE));


        menu.setOnAction( e -> removeFromParent());

        menu.getItems().addAll(mnRemove);
        return menu;
    }

    public void removeFromParent() {
        Pane parent = (Pane) getParent();
        if (parent != null) {
            parent.getChildren().remove(this);
        }
    }
}
