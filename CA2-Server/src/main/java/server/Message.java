/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Bloch
 */
public class Message {

    private Collection<ClientHandler> clients;
    private ClientHandler sender;
    private String message;
    private Collection<ClientHandler> receivers;

    public Message(ClientHandler sender, String str, Collection<ClientHandler> clients)
    {
        this.receivers = new ArrayList<>();
        this.sender = sender;
        this.clients = clients;
        String[] stringsColon = str.split(":");
        setReceivers(stringsColon);        
        setMessage(stringsColon);
    }

    private void setReceivers(String[] stringsColon)
    {        
        if (stringsColon.length <= 1)
        {
            System.out.println("something was wrong with user input");
            return;
        }
        if (stringsColon[1].equalsIgnoreCase("*"))
        {
            System.out.println("setting receivers to all users");
            this.receivers = this.clients;            
            return;
        }

        String[] stringsComma = stringsColon[1].split(",");
        for (String string : stringsComma)
        {
            System.out.println("adding receivers");
            ClientHandler client = findUser(string);
            if (client != null)
            {
                this.receivers.add(client);
            }
        }
    }

    private void setMessage(String[] colonStr)
    {
        if (colonStr.length == 3)
        {
            this.message = "";
            for (int i = 2; i < colonStr.length; i++)
            {
                this.message = colonStr[i];
            }
        } else {
            System.out.println("string did not have a message");
        }
    }

    private ClientHandler findUser(String userName)
    {
        for (ClientHandler client : clients)
        {
            if (client.getName().equalsIgnoreCase(userName))
            {
                return client;
            }
        }
        return null;
    }

    @Override
    public String toString()
    {
        return "MSGRES:" + this.sender.getName() + ":" + this.message;
    }

    public ClientHandler getSender()
    {
        return sender;
    }

    public String getMessage()
    {
        return message;
    }

    public Collection<ClientHandler> getReceivers()
    {
        return receivers;
    }


}
