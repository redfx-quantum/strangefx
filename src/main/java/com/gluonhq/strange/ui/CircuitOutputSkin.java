package com.gluonhq.strange.ui;

import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.shape.Rectangle;

public class CircuitOutputSkin extends SkinBase<CircuitOutput> {


    private Rectangle percentageRect = new Rectangle();
    private Label label = new Label();

    protected CircuitOutputSkin(CircuitOutput control) {
        super(control);
        label.setStyle("-fx-text-fill: black; -fx-alignment: center; -fx-font-size: .8em");

        getSkinnable().stateProperty().addListener( (o, ov, value) -> {
            label.setText( outputValue2String(value.doubleValue()) );
        });
        label.setText( outputValue2String( getSkinnable().getState()) );

        percentageRect.setStyle("-fx-fill: lightgreen; -fx-stroke-width: 0;");
        getChildren().addAll(percentageRect, label);
    }

    private String outputValue2String( double value) {
        if ( value == 0 ) return "Off";
        if ( value == 1 ) return "On";
        return String.format("%.2f%%", value * 100);
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {

        percentageRect.setX(1);
        percentageRect.setWidth(contentWidth);
        double height = contentHeight - contentHeight * getSkinnable().getState() ;
        percentageRect.setY(height+1);
        percentageRect.setHeight(height);

        label.resize(contentWidth,contentHeight);

    }
}
