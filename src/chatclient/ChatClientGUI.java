package chatclient;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ChatClientGUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat_window.fxml"));
        Parent root = loader.load();
        stage.setTitle("Chat Client GUI");
        stage.setScene(new Scene(root));
        EventHandler<WindowEvent> event = new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Controller c = loader.getController();
                c.Quit();
            }
        };
        stage.setOnCloseRequest(event);
        stage.show();
    }
}
