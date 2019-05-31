package org.byron4j.cookbook.javacore.spi;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ServiceLoader;

public class UiApplication extends Application {

    private ServiceLoader<View> views;
    private BorderPane mainBorderPane;

    @Override
    public void init() {
        loadViews();

    }

    private void loadViews() {
        views = ServiceLoader.load(View.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setTitle("Ui Application");

        mainBorderPane = new BorderPane();

        mainBorderPane.setTop(createMenuBar());


        Scene scene = new Scene(new Group());
        scene.setRoot(mainBorderPane);

        stage.setScene(scene);
        stage.show();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu viewMenu = new Menu("Views");
        menuBar.getMenus().add(viewMenu);

        ToggleGroup toggleGroup = new ToggleGroup();

        views.forEach(v -> {
            RadioMenuItem item = new RadioMenuItem(v.getName());
            item.setToggleGroup(toggleGroup);
            item.setOnAction(event -> {
                Label label = new Label(v.getName());
                mainBorderPane.setLeft(label);
                mainBorderPane.setCenter(v.getView());
            });
            viewMenu.getItems().add(item);
        });
        return menuBar;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
