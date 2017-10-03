package server;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageHandler implements Runnable {    

    private Collection<ClientHandler> clients; //might not be needed?    
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
                if (msg.getReceivers().isEmpty())
                {
                    System.out.println("message needs an receiver");
                    msg.getSender().writeMessage("invalid input");
                } else
                {
                    for (ClientHandler receiver : msg.getReceivers())
                    {
                        System.out.println("writing messages to all relevant receivers");
                        receiver.writeMessage(msg.toString());
                    }
                }
            }
        } catch (Exception e)
        {
            Logger.getLogger(MessageHandler.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
