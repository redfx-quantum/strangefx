package com.gluonhq.strange.ui;

import com.gluonhq.strange.Model;
import com.gluonhq.strange.simulator.Gate;
import com.gluonhq.strange.simulator.local.LocalSimulator;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.utils.MaterialIconFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        System.out.println("hello, strange");
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        LocalSimulator sim = new LocalSimulator();
        
        QubitBoard board = new QubitBoard();

        HBox toolbarFiller = new HBox();
        HBox.setHgrow( toolbarFiller, Priority.ALWAYS);

        Button btAddCircuit = new Button("",
                MaterialIconFactory.get().createIcon(MaterialIcon.PLAYLIST_ADD, "2em"));
        btAddCircuit.setOnAction( e -> board.appendQubit());

        ImageView logo = new ImageView("/Gluon_combined_logo_300px.png");
        logo.setPreserveRatio(true);
        logo.setFitWidth(100);
        ToolBar toolbar = new ToolBar(
                btAddCircuit,
                new Separator(),
                GateSymbol.of(Gate.NOT, false),
                GateSymbol.of(Gate.HADAMARD, false),
                toolbarFiller,
                logo
        );

        board.getQubits().addAll( new Qubit(0), new Qubit(1));
        Model.getInstance().refreshRequest().set(true);
        ScrollPane scroller = new ScrollPane(board);
        scroller.setPannable(true);
        scroller.setFitToWidth(true);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(toolbar);
        borderPane.setCenter(scroller);

        Scene scene = new Scene(borderPane, 600, 400);
        scene.getStylesheets().add(Main.class.getResource("/styles.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

}
