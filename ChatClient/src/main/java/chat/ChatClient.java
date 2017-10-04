package chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatClient extends Thread {

    Socket socket;
    private Scanner input;
    private PrintWriter output;
    private IDataReady observer;
    private boolean keepRunning = true;
    Collection<String> userNames = new ArrayList<>();

    /*
  String array split at :
  *ojljopj    
  bob:uuig
  
     */
    public void addObserver(IDataReady observer) {
        this.observer = observer;
    }

    public void login(String name) {
        send("LOGIN:" + name);
    }

    public void sendAll(String msg) {

        String[] strings = msg.split(":");

        if (strings.length >= 2) {
            send("MSG:" + strings[0] + ":" + strings[1]);
            return;
        }

        send("MSG:*:" + msg);

    }

    public void closeConnection() {
        send("LOGOUT:");        
        keepRunning = false;
    }

    //This connects to the server, and start listening for incomming messages
    public void connect(String address, int port) throws IOException {
        socket = new Socket(address, port);
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);
        this.start();
    }

    public void send(String msg) {
        output.println(msg);
    }

    @Override
    public void run() {
        while (keepRunning) {
            String msg = input.nextLine();//BLOKKERENDE KALD
            observer.messageReady(msg);
        }
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String updateClientList(String msg) {
        this.userNames = new ArrayList<>();
        String[] strings = msg.split(":");
        String[] COMMASEPERATED = strings[1].split(",");
//        for (int i = 1; i < strings.length; i++) {
//            userNames.add(strings[i]);
//            
//            System.out.println("first loop " + strings[i]);
//        }
        for (String name : COMMASEPERATED) {
            userNames.add(name);
        }

        String namesToList = "";
        for (String name : userNames) {
            namesToList += name + "\n";
        }
        System.out.println(namesToList);
        return namesToList;

    }
}
