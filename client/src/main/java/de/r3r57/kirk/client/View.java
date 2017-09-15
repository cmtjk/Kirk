package de.r3r57.kirk.client;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class View extends Stage implements Observer {

    private final String TITLE = "Kirk Client";

    private Model model;
    private Controller controller;

    private Button connect;
    private TextField serverAdressInput;
    private TextField portInput;

    boolean connected;
    private TextArea chatArea;
    private ListView<String> onlineUsers;
    private Button send;
    private TextField messageInput;

    private boolean adminMode;

    private VBox mainPane;

    public View(Model model) {

        this.model = model;
        connected = false;
        adminMode = false;

        setFrameOptions();

        Pane rootPane = createContentPane();
        initListenerAndEvents();

        Scene scene = new Scene(rootPane, 650, 550);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("style/stylesheet.css").toString());

        this.setScene(scene);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void registerObserver() {
        model.addObserver(this);
    }

    public void error(String errorMessage) {
        chatArea.appendText("ERROR: " + errorMessage + "\n");
    }

    private void initListenerAndEvents() {

        connect.setOnAction(event -> {
            if (!connected && controller.connect(serverAdressInput.getText().trim(), portInput.getText().trim())) {
                send.setDisable(false);
                connected = true;
                connect.setText("Disconnect");
                connect.setId("disconnect-button");
                serverAdressInput.setDisable(true);
                portInput.setDisable(true);
            } else if (connected && controller.disconnect()) {
                disconnect();
            }
            event.consume();
        });

        send.setOnAction(event -> {
            if (!"".equals(messageInput.getText().trim())) {
                sendMessage();
            }
            event.consume();
        });

        messageInput.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER && !"".equals(messageInput.getText().trim())) {
                send.fire();
                keyEvent.consume();
            }
        });

        this.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (connected) {
                    controller.disconnect();
                    connected = false;
                }
            }

        });
    }

    private void disconnect() {
        serverAdressInput.setDisable(false);
        portInput.setDisable(false);
        send.setDisable(true);
        connected = false;
        connect.setText("Connect");
        connect.setId("connect-button");

    }

    private void sendMessage() {
        if (connected) {
            String[] input = messageInput.getText().trim().split(" ");
            switch (input[0]) {
                case "/name":
                    if (input.length == 2) {
                        controller.changeName(input[1]);
                    } else {
                        chatArea.appendText("Usage: /name <name>\n");
                    }
                    break;
                case "/admin":
                    if (input.length == 2 && "off".equals(input[1])) {
                        adminMode = false;
                        chatArea.appendText("Admin mode: off\n");
                    } else if (input.length == 3 && "on".equals(input[1]) && "ecma".equals(input[2])) {
                        adminMode = true;
                        chatArea.appendText("Admin mode: on\n");
                    } else {
                        chatArea.appendText("Usage: /admin <on/off> [password]\n");
                    }
                    break;
                case "/kick":
                    if (adminMode && input.length == 2) {
                        controller.kick(input[1]);
                    } else {
                        chatArea.appendText("You  have no right to do that.\n");
                    }
                    break;
                case "/f2mission":
                    if (input.length == 2) {
                        controller.getF2Mission(input[1]);
                    } else {
                        chatArea.appendText("Usage: /f2mission <easy|medium|hard>");
                    }
                    break;
                default:
                    controller.sendMessage(messageInput.getText().trim());
                    break;
            }
            messageInput.setText("");
            messageInput.requestFocus();

        } else {

            chatArea.appendText("Not connected.\n");
        }

    }

    public VBox createContentPane() {

        mainPane = new VBox(10);
        mainPane.getStyleClass().add("custom-pane");

        mainPane.getChildren().addAll(createConnectionPane(), createChatArea(), createMessageInput());

        return mainPane;
    }

    private TitledPane createConnectionPane() {

        HBox connectionPane = new HBox(10);
        connectionPane.setAlignment(Pos.CENTER);
        connectionPane.getStyleClass().add("custom-pane");

        Label serverAddress = new Label("Server");
        serverAdressInput = new TextField("127.0.0.1");
        serverAdressInput.setPrefWidth(125);

        Label port = new Label("Port:");
        portInput = new TextField("8971");
        portInput.setPrefWidth(125);

        connect = new Button("Connect");
        connect.setPrefWidth(100);
        connect.setId("connect-button");

        connectionPane.getChildren().addAll(serverAddress, serverAdressInput, port, portInput, connect);

        TitledPane connectionTitledPane = new TitledPane("Connection", connectionPane);
        connectionTitledPane.setCollapsible(false);

        return connectionTitledPane;
    }

    private TitledPane createChatArea() {

        HBox chatAreaPane = new HBox(5);
        chatAreaPane.setAlignment(Pos.CENTER);
        chatAreaPane.getStyleClass().add("custom-pane");

        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);

        onlineUsers = new ListView<>();
        onlineUsers.setEditable(false);
        onlineUsers.setDisable(false);

        chatAreaPane.getChildren().addAll(chatArea, onlineUsers);

        TitledPane chatAreaTitledPane = new TitledPane("Chat", chatAreaPane);
        chatAreaTitledPane.getStyleClass().add("custom-pane");
        chatAreaTitledPane.setCollapsible(false);

        return chatAreaTitledPane;

    }

    private HBox createMessageInput() {
        HBox messageInputPane = new HBox(10);
        messageInputPane.setPadding(new Insets(0, 10, 15, 10));
        messageInputPane.setAlignment(Pos.CENTER);

        messageInput = new TextField();
        messageInput.setPrefWidth(550);

        send = new Button("Send");
        send.setPrefWidth(150);
        send.setMinWidth(100);
        send.setDisable(true);

        messageInputPane.getChildren().addAll(messageInput, send);

        return messageInputPane;
    }

    private void setFrameOptions() {
        setTitle(TITLE);
        setMinWidth(550);
        setMinHeight(450);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update(Observable o, Object arg) {
        if (o != null && o instanceof Model && arg != null) {
            if (arg instanceof String) {
                Platform.runLater(() -> {
                    chatArea.appendText((String) arg + "\n");
                });
            } else if (arg instanceof List) {
                Platform.runLater(() -> {
                    onlineUsers.setItems(FXCollections.observableArrayList((List<String>) arg));
                });
            } else if (arg instanceof Error) {
                Error error = (Error) arg;
                Platform.runLater(() -> {
                    chatArea.appendText(error.getMessage() + "\n");
                    disconnect();
                });
            }

        }

    }

}
