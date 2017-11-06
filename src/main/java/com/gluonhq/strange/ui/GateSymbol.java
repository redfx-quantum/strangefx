package com.gluonhq.strange.ui;

import com.gluonhq.strange.simulator.Gate;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.util.Objects;

public class GateSymbol extends Label {

    private static final DataFormat DRAGGABLE_GATE = new DataFormat("draggable-gate");

//    private final int type;
    private Gate gate;

    public static GateSymbol of( Gate gate ) {
        return new GateSymbol(gate);
    }
    
    private GateSymbol( Gate gate ) {
//        this.type = type;
        this.gate = Objects.requireNonNull(gate);
        setText(gate.getCaption());
        setMinWidth(40);
        setAlignment(Pos.CENTER);
        setStyle("-fx-border-color: rootColor; -fx-padding: 10 5 10 5; -fx-text-fill: rootColor");


        setOnDragDetected(e -> {
            Dragboard db = this.startDragAndDrop(TransferMode.ANY);
            db.setDragView(this.snapshot(null, null));

            ClipboardContent content = new ClipboardContent();
            content.putString(this.getText());
            db.setContent(content);

            content.put(DRAGGABLE_GATE, "");
            e.consume();
        });
        
        this.setOnDragDone(e -> {
            System.out.println("Drag Done");
        });
        
        this.setOnDragDropped(e -> {
            System.out.println("Drag dropped");
        });

    }
    
//    public int getType() {
//        return this.type;
//    }


}
