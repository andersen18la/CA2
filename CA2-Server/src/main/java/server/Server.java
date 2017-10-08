package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private ServerSocket serverSocket;
    private ExecutorService ex;
    private Collection<ClientHandler> clients;
    private MessageHandler messageHandler;

    public Server()
    {
        this.ex = Executors.newFixedThreadPool(15);
        this.clients = new ArrayList<>();
        this.messageHandler = new MessageHandler(clients);
    }
    
    public void startServer(String host, int port)
    {
        try
        {
            this.serverSocket = new ServerSocket();
            this.serverSocket.bind(new InetSocketAddress(host, port));
            //starting messageHandler...
            ex.execute(messageHandler);
            while (true)
            {
                //waiting for new connection.
  //              System.out.println("waiting for connection");
                ex.execute(new ClientHandler(serverSocket.accept(), clients, messageHandler));
                //created new socket for client
            }

        } catch (IOException e)
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
