package com.gluonhq.strange.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
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
        Gate gateNot = new Gate("NOT");
        Gate gateH = new Gate("H");
        ToolBar gatesPane = new ToolBar(gateNot, gateH);

        VBox circuits = new VBox(new Circuit(), new Circuit(), new Circuit());
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
