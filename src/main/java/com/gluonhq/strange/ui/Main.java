package com.gluonhq.strange.ui;

import com.gluonhq.strange.simulator.Gate;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        System.out.println("hello, strange");
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane borderPane = new BorderPane();
        ToolBar gatesPane = new ToolBar(
                GateSymbol.of(Gate.NOT, false),
                GateSymbol.of(Gate.HADAMARD, false)
        );

        VBox circuits = new VBox(new Circuit(0), new Circuit(1), new Circuit(2));
        ScrollPane scroller = new ScrollPane(circuits);
        scroller.setFitToWidth(true);

        borderPane.setTop(gatesPane);
        borderPane.setCenter(scroller);


        Scene scene = new Scene(borderPane, 600, 400);
        scene.getStylesheets().add(Main.class.getResource("/styles.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

}
