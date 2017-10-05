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
    private Collection<String> receivers;

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
            if ("CLIENTLIST".equals(stringsColon[0]))
            {
                for (ClientHandler client : clients)
                {
                    receivers.add(client.getName());
                }
                return;
            }
            //System.out.println("something was wrong with user input");
            return;
        }

        if (stringsColon[1].equalsIgnoreCase("*"))
        {
           // System.out.println("setting receivers to all users");
            for (ClientHandler client : clients)
            {
                receivers.add(client.getName());
            }
            return;
        }

        String[] stringsComma = stringsColon[1].split(",");
       // System.out.println("adding receivers");
        for (String string : stringsComma)
        {
            ClientHandler client = findClient(string);
            if (client != null)
            {
                this.receivers.add(client.getName());
            }
        }
    }

    private void setMessage(String[] colonStr)
    {
        if ("CLIENTLIST".equals(colonStr[0]))
        {
            this.message = clientsToString();
        } else if (colonStr.length >= 3)
        {
            this.message = "";
            for (int i = 2; i < colonStr.length; i++)
            {
                this.message += colonStr[i];
            }
        } else
        {
           // System.out.println("string did not have a message");
        }
    }

    private ClientHandler findClient(String userName)
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

    private String clientsToString()
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

    @Override
    public String toString()
    {
        if (this.message != null && this.message.contains("CLIENTLIST:"))
        {
            return this.message;
        }
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

    public Collection<String> getReceivers()
    {
        return receivers;
    }

}
