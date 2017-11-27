/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2017, Gluon Software
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.strange.ui;

import com.gluonhq.strange.simulator.Gate;
import com.gluonhq.strange.simulator.GateGroup;
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

    GateSymbol(Gate gate, boolean movable) {

        this.gate = Objects.requireNonNull(gate);
        this.movable = movable;
        getStyleClass().setAll("gate-symbol", getStyle(gate.getGroup()));
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

    private String getStyle(GateGroup group ) {
        return group.name().toLowerCase().replaceAll("_", "");
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
