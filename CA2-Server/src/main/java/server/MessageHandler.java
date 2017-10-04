package server;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageHandler implements Runnable {

    private Collection<ClientHandler> clients;  
    public ArrayBlockingQueue<Message> messages;

    public MessageHandler(Collection<ClientHandler> clients)
    {
        this.clients = clients;
        this.messages = new ArrayBlockingQueue<>(50);
    }

    @Override
    public void run()
    {
        System.out.println("inside the message handler");
        Message msg;
        try
        {
            while (true)
            {
                System.out.println("Inside while loop in messageHandler");
                msg = messages.take();
                if (msg.getMessage() == null)
                {
                    msg.getSender().writeMessage("invalid input");
                } else if (msg.getReceivers().isEmpty())
                {
                    if (!msg.getMessage().contains("CLIENTLIST:"))
                    {
                        System.out.println("message needs an receiver");
                        msg.getSender().writeMessage("invalid input");
                    }
                } else
                {
                    System.out.println("writing messages to all relevant receivers");
                    for (String clientName : msg.getReceivers())
                    {
                        ClientHandler client = findClient(clientName);
                        if (client != null)
                        {
                            client.writeMessage(msg.toString());
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            Logger.getLogger(MessageHandler.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private ClientHandler findClient(String name)
    {
        for (ClientHandler client : clients)
        {
            if (name.equals(client.getName()))
            {
                return client;
            }
        }
        return null;

    }

}
