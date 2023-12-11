package chatclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

public class Controller {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;
    
    private Socket socket;
    private PrintWriter writer;
    private ReceiveThread receiveThread;

    @FXML
    private TextField nameField;
    @FXML
    private TextField inputField;
    @FXML
    private Button connectButton;
    @FXML
    private AnchorPane rightPane;
    @FXML
    private TextArea chatArea;

    public void initialize() {

    }
    
    @FXML
    public void exitApplication(ActionEvent event) {
        Platform.exit();
        System.out.print("asdsadasdds");
    }
    
    public void CheckName() {
        if(nameField.getText().equals(""))
            connectButton.setDisable(true);
        else
            connectButton.setDisable(false);
    }
    
    public void Connect() {
        try {
            if(connectButton.getText().equals("Connect")) {
                socket = new Socket(SERVER_HOST, SERVER_PORT);
                writer = new PrintWriter(socket.getOutputStream(), true);

                receiveThread = new ReceiveThread(socket, chatArea);
                receiveThread.start();

                connectButton.setText("Disconnect");
                rightPane.setDisable(false);
                nameField.setDisable(true);
            }
            else {
                receiveThread.Close();
                socket.close();
                connectButton.setText("Connect");
                rightPane.setDisable(true);
                nameField.setDisable(false);
                inputField.setText("");
                chatArea.setText("");
            }
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void Send(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ENTER)) {
            writer.println(inputField.getText());
            inputField.setText("");
        }
    }
}
