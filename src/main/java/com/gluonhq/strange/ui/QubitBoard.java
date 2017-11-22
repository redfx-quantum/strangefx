package com.gluonhq.strange.ui;

import com.gluonhq.strange.Model;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

public class QubitBoard extends VBox {

    private Model model = Model.getInstance();
    private ObservableList<Qubit> qubits = FXCollections.observableArrayList();

    private final int initialQubitNumber;

    public QubitBoard( int initialQubitNumber ) {

        this.initialQubitNumber = initialQubitNumber;
        getChildren().setAll(qubits);

        qubits.addListener( (Observable o) -> {
            getChildren().setAll(qubits);
            model.setNQubits(qubits.size());
            model.refreshRequest().set(true);
        });

        for (int i = 0; i < initialQubitNumber; i++) {
            appendQubit();
        }

    }

    public ObservableList<Qubit> getQubits() {
        return qubits;
    }

    public void appendQubit() {
        qubits.add( new Qubit(qubits.size()));
    }

    public void clear() {
        qubits.forEach(Qubit::clear);
        qubits.removeIf(qb -> qb.getIndex() > (initialQubitNumber-1));
    }
}
