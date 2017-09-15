package de.r3r57.kirk.client;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Model model = new Model();
        View view = new View(model);
        Controller controller = new Controller(view, model);
        view.setController(controller);
        view.registerObserver();

    }
}
