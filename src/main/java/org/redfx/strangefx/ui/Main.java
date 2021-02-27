/*-
 * #%L
 * StrangeFX
 * %%
 * Copyright (C) 2020, 2021 Johan Vos
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Johan Vos nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.redfx.strangefx.ui;

import org.redfx.strangefx.simulator.RenderModel;
import org.redfx.strange.gate.*;
import org.redfx.strange.simulator.local.LocalSimulator;
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
import org.redfx.strangefx.ui.GateSymbol.ControlQubit;

public class Main extends Application {
    
    private final RenderModel model = new RenderModel();

    public static void main(String[] args) {
        System.out.println("hello, strange");
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        LocalSimulator sim = new LocalSimulator(model);
        model.setNQubits(2);
        QubitBoard board = new QubitBoard(model);

        HBox toolbarFiller = new HBox();
        HBox.setHgrow( toolbarFiller, Priority.ALWAYS);

        ImageView logo = new ImageView("/strangelogo.png");
        logo.setPreserveRatio(true);
        logo.setFitWidth(100);

        ToolBar toolbar = new ToolBar(
            createButton( "Append Qubit", e -> board.appendQubit() ),
            createButton( "Clear All", e -> board.clear() ),
            new Separator(),
            GateSymbol.of(new Identity(0), false),
            GateSymbol.of(new X(0), false),
            GateSymbol.of(new Y(0), false),
            GateSymbol.of(new Z(0), false),
            GateSymbol.of(new Hadamard(0), false),
            new Separator(),
            GateSymbol.of(new PartialGate(PartialGate.ControlQubit.ON)),
            toolbarFiller,
            logo
        );

        model.refreshRequest().set(true);

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

    private Button createButton( String tooltip, EventHandler<ActionEvent> eventHandler ) {
        Button button = new Button(tooltip);
        button.setOnAction(eventHandler);
        if ( tooltip != null && !tooltip.trim().isEmpty()) {
            Tooltip t = new Tooltip(tooltip);
            button.setTooltip(t);
        }
        return button;
    }

}
