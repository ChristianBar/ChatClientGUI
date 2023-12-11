package chatclient;

import java.io.*;
import java.net.Socket;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class ReceiveThread extends Thread {
    Socket socket = null;
    boolean close = false;
    TextArea chatArea;
    
    public class Message {
        private String msg;
        public synchronized void setMsg(String m) {msg = m;}
        public synchronized String getMsg() {return msg;}
    }
    
    public ReceiveThread(Socket s, TextArea chat) {
        socket = s;
        chatArea = chat;
    }
    
    public void Close() {
        close = true;
    }
    
    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            final Message msg = new Message();
            String message;
            while (!close && ((message = reader.readLine()) != null)) {
                msg.setMsg(message);
                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        String chatText = chatArea.getText();
                        chatText += msg.getMsg() + "\n";
                        chatArea.setText(chatText);
                    }
                });
            }
        } catch (IOException e) {
            System.out.println("ERRORE: " + e.getMessage());
        }
    }
}
