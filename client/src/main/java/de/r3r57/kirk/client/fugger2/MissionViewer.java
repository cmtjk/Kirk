package de.r3r57.kirk.client.fugger2;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MissionViewer extends Stage {

    private ImageView imageView;

    public MissionViewer() {

        setFrameOptions();

        Pane rootPane = createContentPane();
        initListenerAndEvents();

        Scene scene = new Scene(rootPane, 300, 250);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("style/stylesheet.css").toString());

        this.setScene(scene);
    }

    private VBox createContentPane() {
        VBox mainPane = new VBox(10);
        mainPane.setPadding(new Insets(10, 10, 10, 10));
        mainPane.setAlignment(Pos.CENTER);

        Label header = new Label("Euer Auftrag lautet:");
        imageView = new ImageView();

        mainPane.getChildren().addAll(header, imageView);
        return mainPane;

    }

    public void setImage(Path imagePath) {
        if (imagePath != null && Files.exists(imagePath)) {
            Image image = new Image("file:" + imagePath.toString());
            imageView.setImage(image);
        }

    }

    private void setFrameOptions() {
        setTitle("Fugger 2 - Auftrag");
        setResizable(false);
    }

    private void askOnExitDialog() {

        Alert alertDialog_Alert = new Alert(AlertType.NONE);
        alertDialog_Alert.setTitle("Schließen");

        alertDialog_Alert.setHeaderText("Fenster wirklich schließen?");

        ButtonType no_Button = new ButtonType("Nein", ButtonData.NO);
        ButtonType yes_Button = new ButtonType("Ja", ButtonData.YES);

        alertDialog_Alert.getButtonTypes().addAll(no_Button, yes_Button);

        Optional<ButtonType> result = alertDialog_Alert.showAndWait();
        if (result.isPresent() && result.get() == yes_Button) {
            close();
        }
    }

    private void initListenerAndEvents() {

        this.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {

                askOnExitDialog();
                event.consume();
            }
        });
    }
}
