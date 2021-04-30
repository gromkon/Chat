package client;

import client.controller.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Controller controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent root = (Parent) loader.load(getClass().getResourceAsStream("/fxml/sample.fxml"));
        stage.setTitle("Chat");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();

        controller = loader.getController();
        stage.setOnCloseRequest(event -> {
            controller.dispose();
            Platform.exit();;
            System.exit(0);
        });
    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }
}