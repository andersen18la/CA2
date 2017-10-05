package chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChatClient extends Thread {

    Socket socket;
    private Scanner input;
    private PrintWriter output;
    private IObserver observer;
    private boolean keepRunning = true;
    private List<String> userNames;

    public ChatClient()
    {
    }

    public ChatClient(IObserver observer)
    {
        this.observer = observer;
    }

    /*
  String array split at :
  *ojljopj    
  bob:uuig
  
     */
    public void addObserver(IObserver observer)
    {
        this.observer = observer;
    }

    public void login(String name)
    {
        send("LOGIN:" + name);
    }

    public void sendAll(String msg)
    {
        String[] strings = msg.split(":");

        //message to all
        if (strings.length == 1)
        {
            send("MSG:*:" + msg);
            return;
        }
        //private message:
        if (strings.length == 2)
        {
            String[] commaSep = strings[0].split(",");
            String receivers = "";
            StringBuilder sb = new StringBuilder();

            for (String name : commaSep)
            {
                sb.append(name).append(",");
            }
            if (sb.toString().charAt(sb.length() - 1) == ',')
            {
                sb.deleteCharAt(sb.length() - 1);
            }
            observer.showOwnPrivateMessage("CLIENT: Whispered " + msg);
            send("MSG:" + sb.toString() + ":" + strings[1]);
            return;
        }

        //too many colons...
        String noColonDamnit = "";
        for (String string : strings)
        {
            noColonDamnit += string;
        }
        send("MSG:*:" + noColonDamnit);

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
        observer.connectedToServer("CLIENT: Connected to the server! Please login");
    }

    public void send(String msg)
    {
        output.println(msg);
    }

    private String msgHandler(String msg)
    {
        String[] strings = msg.split(":");
//        String tag = strings[0];
//        String name = strings[1];
//        String message = strings[2];        

        if (strings.length == 3)
        {
            return "<" + strings[1] + ">" + strings[2];
        }

        if (strings[0].equals("CLIENTLIST"))
        {
            updateUserNames(strings);
        }
        return null;
    }

    @Override
    public void run()
    {
        try
        {
            while (keepRunning)
            {
                String msg = input.nextLine();
                String handledMessage = msgHandler(msg);
                if (handledMessage == null)
                {
                    observer.updateClientList(clientListToString());
                } else
                {
                    observer.messageReady(handledMessage);
                }
            }
            socket.close();
        } catch (Exception ex)
        {
            observer.connectionLost("CLIENT: Connection to the server is dead.");
            this.userNames = null;
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

    private void updateUserNames(String[] stringsColon)
    {
        if (this.userNames == null)
        {
            this.userNames = new ArrayList<>();
            observer.loggedIn("CLIENT: Logged in succesfully!");
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
            String newUser = newUserNames.get(newUserNames.size() - 1);
            observer.clientConnected(newUser + " joined the server!");
        }

        if (newUserNames.size() < userNames.size())
        {
            //someone left...                
            String name = findWhoLeft(newUserNames);
            observer.clientDisconnected(name + " left the server!");
        }

        userNames = newUserNames;
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
}
