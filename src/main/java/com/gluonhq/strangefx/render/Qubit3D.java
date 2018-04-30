package com.gluonhq.strangefx.render;

import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.fxyz3d.geometry.Point3D;
import org.fxyz3d.shapes.primitives.FrustumMesh;
import org.fxyz3d.shapes.primitives.SegmentedSphereMesh;

/**
 *
 * @author JosePereda
 */
public class Qubit3D extends Group {
    
    private double mouseOldX, mouseOldY = 0;
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private Group rodSphere;

    public Qubit3D() {
        createQubit();
    }

    private void createQubit() {
        PerspectiveCamera camera = new PerspectiveCamera(true);        
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.getTransforms().addAll(rotateX, rotateY, new Translate(0, 0, -200));
     
        FrustumMesh plane = new FrustumMesh(50, 50, 1, 1, new Point3D(0, -0.5f, 0), new Point3D(0, 0.5f, 0));
        plane.setMaterial(new PhongMaterial(Color.web("#ccdd3320")));
        
        SegmentedSphereMesh innerSphere = new SegmentedSphereMesh(40, 0, 0, 50, new Point3D(0, 0, 0));
        innerSphere.setMaterial(new PhongMaterial(Color.web("#ff800080")));
        
        SegmentedSphereMesh frameSphere = new SegmentedSphereMesh(20, 0, 0, 50, new Point3D(0, 0, 0));
        frameSphere.setMaterial(new PhongMaterial(Color.BLACK));
        frameSphere.setDrawMode(DrawMode.LINE);
        
        FrustumMesh rod = new FrustumMesh(2, 2, 1, 1, new Point3D(0, 0, 0), new Point3D(0, -50, 0));
        rod.setMaterial(new PhongMaterial(Color.web("#0080ff")));
        
        SegmentedSphereMesh smallSphere = new SegmentedSphereMesh(20, 0, 0, 4, new Point3D(0, -50, 0));
        smallSphere.setMaterial(new PhongMaterial(Color.web("#0080ff")));
        
        rodSphere = new Group(smallSphere, rod);
        Group group = new Group(plane, rodSphere, innerSphere, frameSphere, new AmbientLight(Color.BISQUE));
        
        SubScene subScene = new SubScene(group, 100, 100, true, SceneAntialiasing.BALANCED);
        subScene.setCamera(camera);
        
        subScene.setOnMousePressed(event -> {
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });

        subScene.setOnMouseDragged(event -> {
            rotateX.setAngle(rotateX.getAngle() - (event.getSceneY() - mouseOldY));
            rotateY.setAngle(rotateY.getAngle() + (event.getSceneX() - mouseOldX));
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });
        
        getChildren().add(subScene);
    }
    
    public void rotateRod(Rotate rotate) {
        rodSphere.getTransforms().setAll(rotate);
    }
}
