/*-
 * #%L
 * StrangeFX
 * %%
 * Copyright (C) 2020 Johan Vos
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
package org.redfx.strangefx.render;

import org.redfx.strangefx.ui.Main;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
//import org.fxyz3d.geometry.Point3D;
//import org.fxyz3d.shapes.primitives.FrustumMesh;
//import org.fxyz3d.shapes.primitives.SegmentedSphereMesh;

/**
 *
 * @author JosePereda
 */
public class Qubit3D extends Group {
    
    private int currentStep = 0;
    private double mouseOldX, mouseOldY = 0;
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private Group rodSphere;
    private final Rotate myRotate = new Rotate(90, 0, 0, 0, Rotate.Z_AXIS);

    public Qubit3D() {
        createQubit();
    }
    
    private void createQubit() {
        PerspectiveCamera camera = new PerspectiveCamera(true);        
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.getTransforms().addAll(rotateX, rotateY, new Translate(0, 0, -200));
//     
//        FrustumMesh plane = new FrustumMesh(50, 50, 1, 1, new Point3D(0, -0.5f, 0), new Point3D(0, 0.5f, 0));
//        plane.setMaterial(new PhongMaterial(Color.web("#ccdd3320")));
//        
//        SegmentedSphereMesh innerSphere = new SegmentedSphereMesh(40, 0, 0, 50, new Point3D(0, 0, 0));
//        innerSphere.setMaterial(new PhongMaterial(Color.web("#ff800080")));
//        
//        SegmentedSphereMesh frameSphere = new SegmentedSphereMesh(20, 0, 0, 50, new Point3D(0, 0, 0));
//        frameSphere.setMaterial(new PhongMaterial(Color.BLACK));
//        frameSphere.setDrawMode(DrawMode.LINE);
//        
//        FrustumMesh rod = new FrustumMesh(2, 2, 1, 1, new Point3D(0, 0, 0), new Point3D(50, 0, 0));
//        rod.setMaterial(new PhongMaterial(Color.web("#0080ff")));
//        
//        SegmentedSphereMesh smallSphere = new SegmentedSphereMesh(20, 0, 0, 4, new Point3D(50, 0, 0));
//        smallSphere.setMaterial(new PhongMaterial(Color.web("#0080ff")));
//        
//        rodSphere = new Group(smallSphere, rod);
//        Group group = new Group(plane, rodSphere, innerSphere, frameSphere, new AmbientLight(Color.BISQUE));
//        
//        SubScene subScene = new SubScene(group, 100, 100, true, SceneAntialiasing.BALANCED);
//        subScene.setCamera(camera);
//        
//        subScene.setOnMousePressed(event -> {
//            mouseOldX = event.getSceneX();
//            mouseOldY = event.getSceneY();
//        });
//
//        subScene.setOnMouseDragged(event -> {
//            rotateX.setAngle(rotateX.getAngle() - (event.getSceneY() - mouseOldY));
//            rotateY.setAngle(rotateY.getAngle() + (event.getSceneX() - mouseOldX));
//            mouseOldX = event.getSceneX();
//            mouseOldY = event.getSceneY();
//        });
//        
//        getChildren().add(subScene);
//        
//        rodSphere.getTransforms().setAll(myRotate);
    }
    
    public int getCurrentStep() {
        return currentStep;
    }
    
    public void incrementStep() {
        currentStep++;
    }

    public void resetStep() {
        currentStep = 0;
    }

    public void flip() {
        myRotate.setAngle(- myRotate.getAngle());
    }
    
        
    public void show() {
        BorderPane bp = new BorderPane(this);
         Scene scene = new Scene(bp, 300, 300);
        scene.getStylesheets().add(Main.class.getResource("/styles.css").toExternalForm());
                Stage stage = new Stage();
        stage.setTitle("StrangeFX rendering");
        stage.setScene(scene);
        System.out.println("show stage...");
        stage.show();
        System.out.println("showed scene");

    }
}
