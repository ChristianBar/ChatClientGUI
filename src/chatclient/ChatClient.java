package chatclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatClient extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat_window.fxml"));
        Parent root = loader.load();
        stage.setTitle("Chat Client");
        stage.setScene(new Scene(root));
        stage.setOnCloseRequest(event -> {
            System.out.println("Stage is closing");
        });
        stage.show();
    }
}
