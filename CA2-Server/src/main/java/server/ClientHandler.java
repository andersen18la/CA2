package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

    private String name;
    private Socket clientSocket;
    private Scanner in;
    private PrintWriter out;
    private Collection<ClientHandler> clients;
    private ExecutorService ex;
    private MessageHandler messageHandler;

    public ClientHandler(Socket clientSocket, Collection<ClientHandler> clients, ExecutorService ex, MessageHandler ms)
    {
        this.ex = ex;
        this.clientSocket = clientSocket;
        this.clients = clients;
        this.messageHandler = ms;
    }

    @Override
    public void run()
    {

        System.out.println("waiting for input");
        try
        {
            this.in = new Scanner(clientSocket.getInputStream());
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e)
        {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, e);
        }        
        String input;
        String[] strings = null;
        // runnable quits if input contains a "'" and if input is correct,
        // but name is already in the list.
        // you break out of the loop if input is correct.
        while ((input = in.nextLine()) != null)
        {
            if (input.contains(","))
            {
                System.out.println("',' should not be used");
                stopConnection();
                return;
            }

            strings = input.split(":");
            if (strings.length == 2 && strings[0].equals("LOGIN"))
            {
                for (ClientHandler client : clients)
                {
                    if (strings[1].equalsIgnoreCase(client.getName()))
                    {
                        System.out.println("Name was already taken");
                        stopConnection();
                        return;
                    }
                }
                break;
            }
            System.out.println("input was wrong");
        }
        System.out.println("adding client to list");
        this.name = strings[1];
        addClient(this);

        // clientListString() method loops, so init it here, so it does not
        // loop for every client.
        String clientListString = clientListString();
        for (ClientHandler client : clients)
        {
            client.writeMessage(clientListString);
        }
        // user loop is a loop, you can break out if you type "LOGOUT:" 
        // or somethings goes wrong
        userLoop();

        System.out.println("closing clientSocket for " + this.name);        
        stopConnection();
    }

    // Could be move out to a new class that implements runnable? Would that be
    // smart?
    public void userLoop()
    {
        try
        {
            String input;
            while ((input = in.nextLine()) != null)
            {
                if ("LOGOUT:".equals(input))
                {
                    System.out.println("client typed LOGOUT:...");
                    clients.remove(this);
                    String clientListStr = clientListString();
                    for (ClientHandler client : clients)
                    {
                        client.writeMessage(clientListStr);
                    }
                    return;
                }
                //creating new message with an sender, the input string and a list of Clients.
                Message msg = new Message(this, input, this.clients);
                messageHandler.messages.put(msg);
            }

        } catch (Exception e) //catches all exceptions... mainly there to keep the list correct.
        {
            System.out.println("her?");
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            clients.remove(this);
            String clientListStr = clientListString();
            for (ClientHandler client : clients)
            {
                client.writeMessage(clientListStr);
            }
        }
    }

    public synchronized void writeMessage(String str)
    {
        out.println(str);
    }

    public synchronized boolean addClient(ClientHandler client)
    {
        return clients.add(client);
    }

    public synchronized boolean removeClient(ClientHandler client)
    {
        return this.clients.remove(client);
    }

    public String getName()
    {
        return this.name;
    }

    public String clientListString()
    {
        String clientList = "CLIENTLIST:";
        for (ClientHandler client : clients)
        {
            clientList += client.getName() + ",";
        }
        StringBuilder sb = new StringBuilder(clientList);
        sb.deleteCharAt(clientList.length() - 1);
        return sb.toString();

    }

    public void stopConnection()
    {
        try
        {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException ex)
        {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
