package chatclient;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import org.json.JSONArray;
import org.json.JSONObject;

public class ReceiveThread extends Thread {
    private Socket socket = null;
    private boolean close = false;
    private final TextArea chatArea;
    private final TextArea usersArea;
    private final ArrayList<MessageJson> messagesJson;
    private boolean locked = false;
    
    public class MessageJson {
        private String msgJson;
        public synchronized void setMsgJson(String m) {msgJson = m;}
        public synchronized String getMsgJson() {return msgJson;}
    }
    
    public ReceiveThread(Socket s, TextArea chat, TextArea users) {
        messagesJson = new ArrayList<>();
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
            String message;
            while (!close && ((message = reader.readLine()) != null)) {
                MessageJson msgJson = new MessageJson();
                msgJson.setMsgJson(message);
                while(locked) try {
                    Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ReceiveThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                locked = true;
                messagesJson.add(msgJson);
                locked = false;
                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        try {
                            while(locked) Thread.sleep(10);
                            locked = true;
                            for(int i=0; i<messagesJson.size(); i++)
                            {
                                String chatText = chatArea.getText();
                                String json = messagesJson.get(i).getMsgJson();
                                
                                System.out.println(json);
                                JSONObject obj = new JSONObject(json);
                                
                                JSONArray arr = obj.optJSONArray("messages", new JSONArray());
                                for(int j=0; j<arr.length(); j++) {
                                    JSONObject msg = arr.getJSONObject(j);
                                    chatText = msg.getString("date")
                                            + " \t" + msg.getString("name")
                                            + ": \t" + msg.getString("value")  + "\n"
                                            + chatText;
                                }
                                chatArea.setText(chatText);
                                
                                String usersString = "";
                                JSONArray clients = obj.optJSONArray("users", new JSONArray());
                                for(int j=0; j<clients.length(); j++) {
                                    JSONObject msg = clients.getJSONObject(j);
                                    usersString += msg.getString("name") + "\n";
                                }
                                if(clients.length() > 0) usersArea.setText(usersString);
                            }
                            messagesJson.clear();
                            locked = false;
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ReceiveThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
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
