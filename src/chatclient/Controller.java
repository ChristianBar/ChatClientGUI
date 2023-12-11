package chatclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import org.json.JSONArray;
import org.json.JSONObject;

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
    private Button clearButton;
    @FXML
    private AnchorPane rightPane;
    @FXML
    private TextArea chatArea;
    @FXML
    private TextArea usersArea;

    public void initialize() {

    }
    
    public void Quit() {
        try {
            if(receiveThread != null) receiveThread.Close();
            if(socket != null) socket.close();
            Platform.exit();
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void CheckName() {
        if(nameField.getText().equals(""))
            connectButton.setDisable(true);
        else
            connectButton.setDisable(false);
    }
    
    public void Connect() {
        try {
            if(connectButton.getText().equals("Connetti")) {
                socket = new Socket(SERVER_HOST, SERVER_PORT);
                writer = new PrintWriter(socket.getOutputStream(), true);
                
                receiveThread = new ReceiveThread(socket, chatArea, usersArea);
                receiveThread.start();
                
                Send(nameField.getText() + " si è connesso");

                connectButton.setText("Disconnetti");
                clearButton.setDisable(false);
                usersArea.setDisable(false);
                rightPane.setDisable(false);
                nameField.setDisable(true);
                
                new Alert(Alert.AlertType.NONE, "Connesso!", ButtonType.OK).show();
            }
            else {
                receiveThread.Close();
                socket.close();
                connectButton.setText("Connetti");
                clearButton.setDisable(true);
                rightPane.setDisable(true);
                nameField.setDisable(false);
                usersArea.setDisable(true);
                usersArea.setText("");
                inputField.setText("");
                chatArea.setText("");
            }
        } catch (IOException ex) {
            new Alert(Alert.AlertType.NONE, "Errore di connessione!", ButtonType.OK).show();
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void ClearChat() {
        chatArea.setText("");
    }
    
    public void CheckSend(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ENTER)) {
            Send(inputField.getText());
            inputField.setText("");
        }
    }
    
    public void Send(String string) {
        JSONObject msg = new JSONObject();
        msg.put("name", nameField.getText());
        msg.put("value", string);
        msg.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        JSONArray msgs = new JSONArray();
        msgs.put(msg);
        JSONObject obj = new JSONObject();
        obj.put("messages", msgs);
        writer.println(obj.toString());
    }
}
