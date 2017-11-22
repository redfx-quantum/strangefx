package com.gluonhq.strange.ui;

import com.gluonhq.strange.Model;
import com.gluonhq.strange.simulator.Gate;
import com.gluonhq.strange.simulator.local.LocalSimulator;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.utils.MaterialIconFactory;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
        
        QubitBoard board = new QubitBoard(2);

        HBox toolbarFiller = new HBox();
        HBox.setHgrow( toolbarFiller, Priority.ALWAYS);

        ImageView logo = new ImageView("/Gluon_combined_logo_300px.png");
        logo.setPreserveRatio(true);
        logo.setFitWidth(100);

        ToolBar toolbar = new ToolBar(
            createButton( MaterialIcon.PLAYLIST_ADD, e -> board.appendQubit() ),
            new Separator(),
            GateSymbol.of(Gate.IDENTITY, false),
            GateSymbol.of(Gate.NOT, false),
            GateSymbol.of(Gate.CNOT, false),
            GateSymbol.of(Gate.SWAP, false),
            GateSymbol.of(Gate.HADAMARD, false),
            toolbarFiller,
            logo
        );

        Model.getInstance().refreshRequest().set(true);

        ScrollPane scroller = new ScrollPane(board);
        scroller.setPannable(true);
        scroller.setFitToWidth(true);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(toolbar);
        borderPane.setCenter(scroller);

        Scene scene = new Scene(borderPane, 800, 600);
        scene.getStylesheets().add(Main.class.getResource("/styles.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    private Button createButton(MaterialIcon icon, EventHandler<ActionEvent> eventHandler ) {
        Button button =  MaterialIconFactory
                            .get()
                            .createIconButton(icon, "", "2em", "", ContentDisplay.GRAPHIC_ONLY);
        button.setOnAction(eventHandler);
        return button;
    }

}
