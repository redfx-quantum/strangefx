package com.gluonhq.strange.ui;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

public class CircuitBoard extends VBox {

    private ObservableList<Circuit> circuits = FXCollections.observableArrayList();

    public CircuitBoard() {
        getChildren().setAll(circuits);
        circuits.addListener( (Observable o) -> {
            getChildren().clear();
            getChildren().setAll(circuits);
        });
    }

    public ObservableList<Circuit> getCircuits() {
        return circuits;
    }
}
