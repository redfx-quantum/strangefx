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

        this.application = new RenderApplication(nq);

      //  this.application.setBoard(board);
    }

    public void show() {
        application.launchMe();
       // RenderApplication.launch(RenderApplication.class);
    }



    public static class RenderApplication extends Application {

        private int nq;
        private QubitBoard board;

        public RenderApplication() {}

        public RenderApplication(int nq) {
            this.nq = nq;
        }

        public void setBoard(QubitBoard qb) {
            this.board = qb;
        }
        @Override
        public void start(Stage stage) throws Exception {
            QubitBoard board = new QubitBoard(nq);

            Scene scene = new Scene(board);
            stage.setScene(scene);
            stage.show();
        }

        public void launchMe() {
            RenderApplication.launch();
        }
    }

}
