package chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatClient extends Thread {

    Socket socket;
    private Scanner input;
    private PrintWriter output;
    private IDataReady observer;
    private boolean keepRunning = true;
    private List<String> userNames;

    public void addObserver(IDataReady observer)
    {
        this.observer = observer;
    }

    public void login(String name)
    {
        send("LOGIN:" + name);
    }

    public void closeConnection()
    {
        send("LOGOUT:");
        keepRunning = false;
    }

    //This connects to the server, and start listening for incomming messages
    public void connect(String address, int port) throws IOException
    {
        socket = new Socket(address, port);
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);

        this.start();
    }

    public void send(String msg)
    {
        output.println(msg);
    }

    @Override
    public void run()
    {
        try
        {
            while (keepRunning)
            {
                String msg = input.nextLine();
                observer.DataReady(msg);
            }
            socket.close();
        } catch (Exception ex)
        {            
            this.userNames = null;
            this.keepRunning = false;
            exit();
        }
    }
    
    private void exit(){
        try
        {
            socket.close();
        } catch (IOException ex)
        {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //should this return a list instead?
    public String clientListToString()
    {
        String namesToList = "";
        for (String name : userNames)
        {
            namesToList += name + "\n";
        }
        return namesToList;
    }

    public String updateUserNames(String[] stringsColon)
    {
        if (this.userNames == null)
        {
            this.userNames = new ArrayList<>();
        }

        List<String> newUserNames = new ArrayList<>();
        String[] commaSeperatedNames = stringsColon[1].split(",");

        for (String commaSeperatedName : commaSeperatedNames)
        {
            newUserNames.add(commaSeperatedName);
        }
        if (newUserNames.size() > userNames.size())
        {
            //someone joined...
            userNames = newUserNames;
            String newUser = newUserNames.get(newUserNames.size() - 1);
            return newUser + " joind the server";
        }

        if (newUserNames.size() < userNames.size())
        {
            String name = findWhoLeft(newUserNames);
            userNames = newUserNames;
            return name + " left the server!";
        }
        return null;
    }

    private String findWhoLeft(List<String> newUserNames)
    {
        for (String userName : userNames)
        {
            String name = findOldUser(userName, newUserNames);
            if (name == null)
            {
                return userName;
            }
        }
        return null;
    }

    private String findOldUser(String name, List<String> newUserNames)
    {
        for (String userName : newUserNames)
        {
            if (name.equals(userName))
            {
                return userName;
            }
        }
        return null;
    }

    public List<String> getUserNames()
    {
        return userNames;
    }

    public boolean isKeepRunning()
    {
        return keepRunning;
    }
    
    

}
