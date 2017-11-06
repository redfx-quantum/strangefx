package com.gluonhq.strange.ui;

import com.gluonhq.strange.Model;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

public class CircuitBoard extends VBox {

    private Model model = Model.getInstance();
    private ObservableList<Circuit> circuits = FXCollections.observableArrayList();

    public CircuitBoard() {
        getChildren().setAll(circuits);
        circuits.addListener( (Observable o) -> {
            getChildren().clear();
            getChildren().setAll(circuits);
            model.setNQubits(circuits.size());
        });
    }

    public ObservableList<Circuit> getCircuits() {
        return circuits;
    }
}
