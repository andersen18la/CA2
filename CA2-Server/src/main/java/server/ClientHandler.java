package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

    private String name;
    private Socket clientSocket;
    private Scanner in;
    private PrintWriter out;
    private Collection<ClientHandler> clients;    
    private MessageHandler messageHandler;

    public ClientHandler(Socket clientSocket, Collection<ClientHandler> clients, MessageHandler ms)
    {        
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
        //this write is only there for telnet...
        writeMessage("Connected to server...");
        
        if (isLoggedIn() == false)
        {
            stopConnection();
            return;
        }
        System.out.println("adding client to list");
        addClient(this);
        sendClientList();
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
                    sendClientList();
                    return;
                }
                //creating new message with an sender, the input string and a list of Clients.
                sendMessage(input);
            }

        } catch (Exception e) //catches all exceptions... mainly there to keep the list correct.
        {
            System.out.println("her?");
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, e);
            clients.remove(this);
            sendClientList();
        }
    }

    private boolean isLoggedIn()
    {
        String input;
        String[] strings;
        // runnable returns if input contains a "," and if input is correct,
        // but name is already in the list.
        // you break out of the loop if input is correct.
        // SHOULD WE REMOVE THIS WHILE LOOP?
        if ((input = in.nextLine()) != null)
        {
            if (input.contains(",") || !input.contains(":"))
            {
                return false;
            }

            strings = input.split(":");
            if (strings.length != 2)
            {
                return false;
            }
            if (strings.length == 2 && strings[0].equals("LOGIN"))
            {
                if (isNameTaken(strings[1]))
                {
                    return false;
                } else
                {
                    this.name = strings[1];
                    return true;
                }
            }
        }
        return false;
    }

    public void sendClientList()
    {
        Message msg = new Message(this, "CLIENTLIST:", clients);
        try
        {
            messageHandler.messages.put(msg);
        } catch (InterruptedException ex)
        {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendMessage(String input) throws InterruptedException
    {
        Message msg = new Message(this, input, this.clients);
        messageHandler.messages.put(msg);

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

    private boolean isNameTaken(String name)
    {
        for (ClientHandler client : clients)
        {
            if (name.equalsIgnoreCase(client.getName()))
            {
                return true;
            }
        }
        return false;
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
