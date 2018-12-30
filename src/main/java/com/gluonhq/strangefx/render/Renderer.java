package com.gluonhq.strangefx.render;

import com.gluonhq.strange.*;
import com.gluonhq.strange.ui.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;

public class Renderer {

    private RenderApplication application;

    public static void renderProgram (Program p) {
        Renderer renderer = new Renderer(p);
        renderer.show();
    }

    public Renderer(Program p) {
        int nq = p.getNumberQubits();
        QubitBoard board = new QubitBoard(nq);
        this.application = new RenderApplication(board);
    }

    public void show() {
        RenderApplication.launch();
    }



    public class RenderApplication extends Application {

        private QubitBoard board;

        public RenderApplication(QubitBoard board) {
            this.board = board;
        }

        @Override
        public void start(Stage stage) throws Exception {
            Scene scene = new Scene(board);
            stage.setScene(scene);
            stage.show();
        }

    }

}
