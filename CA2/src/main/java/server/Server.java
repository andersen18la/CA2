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
    private Collection<ClientHandler> clientList;

    public Server()
    {
        this.ex = Executors.newCachedThreadPool();
        this.clientList = new ArrayList<>();
    }

    public void startServer(String host, int port)
    {
        try
        {
            this.serverSocket = new ServerSocket();
            this.serverSocket.bind(new InetSocketAddress(host, port));
            
            while(true){
                
                ClientHandler client = new ClientHandler(serverSocket.accept(), clientList);
                ex.execute(client);
                
            }

        } catch (IOException ex)
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
