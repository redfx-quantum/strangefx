package com.gluonhq.strange.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
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
        borderPane.setTop(gatesPane);
        
        
        
        Scene scene = new Scene (borderPane);
        stage.setScene(scene);
        stage.show();
    }

}
