package com.gluonhq.strange.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class Gate extends Label {

    private static final DataFormat DRAGGABLE_GATE = new DataFormat("draggable-gate");

    public Gate(String caption) {
        setText(caption);
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

    }


}
