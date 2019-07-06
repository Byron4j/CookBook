package org.byron4j.cookbook.javacore.spi;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class DefaultView implements View {
    @Override
    public String getName() {
        return "Default View";
    }

    @Override
    public Node getView() {
        Label label = new Label("The label");
        label.setPadding(new Insets(5, 10, 0, 0));
        TextField text = new TextField();

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(50, 50, 50, 50));
        hbox.getChildren().add(label);
        hbox.getChildren().add(text);
        return hbox;
    }
}
