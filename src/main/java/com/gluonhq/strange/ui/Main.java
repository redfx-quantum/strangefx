package com.gluonhq.strange.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        System.out.println ("hello, strange");
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane borderPane = new BorderPane();
        Button gateNot = new Button("NOT");
        Button gateH = new Button ("H");
        FlowPane gatesPane = new FlowPane(gateNot, gateH);

        VBox circuits = new VBox( new Circuit(), new Circuit(), new Circuit());
        ScrollPane scroller = new ScrollPane(circuits);
//        scroller.setFitToHeight(true);
        scroller.setFitToWidth(true);

        borderPane.setTop(gatesPane);
        borderPane.setCenter(scroller);


        Scene scene = new Scene (borderPane);


        scene.getStylesheets().add(Main.class.getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

}
