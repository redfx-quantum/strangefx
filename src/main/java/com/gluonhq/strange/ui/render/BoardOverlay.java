package com.gluonhq.strange.ui.render;

import com.gluonhq.strange.ui.*;
import javafx.beans.*;
import javafx.geometry.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

public class BoardOverlay extends Region {

    private GateSymbol symbol;

    public BoardOverlay(GateSymbol symbol) {
        this.symbol = symbol;
        this.symbol.boundsInParentProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                BoardOverlay me = BoardOverlay.this;
                me.getChildren().clear();
                Bounds bp = me.getBoundsInParent();
                Rectangle rect = new Rectangle(bp.getMinX(),bp.getMinY(),40, 40 * symbol.spanWires);
                rect.setFill(Color.YELLOWGREEN);
                BoardOverlay.this.getChildren().setAll(rect);
            }
        });
    }



}
