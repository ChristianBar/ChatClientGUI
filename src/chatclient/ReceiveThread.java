package chatclient;

import java.io.*;
import java.net.Socket;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import org.json.JSONArray;
import org.json.JSONObject;

public class ReceiveThread extends Thread {
    Socket socket = null;
    boolean close = false;
    TextArea chatArea;
    TextArea usersArea;
    
    public class MessageJson {
        private String msgJson;
        public synchronized void setMsgJson(String m) {msgJson = m;}
        public synchronized String getMsgJson() {return msgJson;}
    }
    
    public ReceiveThread(Socket s, TextArea chat, TextArea users) {
        socket = s;
        chatArea = chat;
        usersArea = users;
    }
    
    public void Close() {
        close = true;
    }
    
    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            final MessageJson msgJson = new MessageJson();
            String message;
            while (!close && ((message = reader.readLine()) != null)) {
                msgJson.setMsgJson(message);
                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        String chatText = chatArea.getText();
                        String json = msgJson.getMsgJson();
                        
                        System.out.println(json);
                        JSONObject obj = new JSONObject(json);
                        
                        JSONArray arr = obj.optJSONArray("messages", new JSONArray());
                        for(int i=0; i<arr.length(); i++) {
                            JSONObject msg = arr.getJSONObject(i);
                            chatText = msg.getString("date")
                                    + " \t" + msg.getString("name") 
                                    + ": \t" + msg.getString("value")  + "\n"
                                    + chatText;
                        }
                        chatArea.setText(chatText);
                        
                        String usersString = "";
                        JSONArray clients = obj.optJSONArray("users", new JSONArray());
                        for(int i=0; i<clients.length(); i++) {
                            JSONObject msg = clients.getJSONObject(i);
                            usersString += msg.getString("name") + "\n";
                        }
                        usersArea.setText(usersString);
                    }
                });
            }
        } catch (IOException e) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    new Alert(Alert.AlertType.NONE, "Disconnesso!", ButtonType.OK).show();
                }
            });

            System.out.println(e.getMessage());
        }
    }
}
