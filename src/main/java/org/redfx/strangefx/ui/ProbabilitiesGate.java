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

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.redfx.strange.Complex;
import org.redfx.strange.Program;
import org.redfx.strange.Result;
import org.redfx.strange.Step;

/**
 * A special implementation of a Gate that is not altering the probability vector, but that
 * is used in rendering.
 * @author johan
 */
public class ProbabilitiesGate extends PseudoGate {

    private Step step;

    public ProbabilitiesGate(Step step) {
        this.step = step;
    }

    public Region createUI () {
        System.err.println("Prob for step " + step.getIndex());
        Program program = step.getProgram();
        Result result = program.getResult();
        Complex[] ip = result.getIntermediateProbability(step.getIndex());
        if (ip == null) throw new IllegalArgumentException ("Can not retrieve probabilities for step "+step.getIndex());
        int nq = program.getNumberQubits();
        int N = 1 << nq;
        double deltaY = (66. * nq - 10 + 38) / N;
        System.err.println("n = " + nq + " and N = " + N + ", dY = " + deltaY);
        Group answer = new Group();

        Rectangle rect2 = new Rectangle(0, 0, 40, 66 * nq - 10 + 38);
        rect2.setFill(Color.WHITE);
        rect2.setStroke(Color.BLUE);
        rect2.setStrokeWidth(1);
        answer.getChildren().add(rect2);

        for (int i = 0; i < N; i++) {
            double startY = i * deltaY;
            Rectangle minibar = new Rectangle(1, i * deltaY, 38 * ip[i].abssqr(), deltaY - 1);
            minibar.setFill(Color.GREEN);
            Line l = new Line(1, startY, 39, startY);
            l.setFill(Color.LIGHTGRAY);
            l.setStrokeWidth(1);
            answer.getChildren().add(l);
            answer.getChildren().add(minibar);
        }
        Pane region = new Pane();
        region.getChildren().add(answer);
        return region;
    }


}
