package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

    //måske en anden klasse i listen?
    private String name;
    private Collection<ClientHandler> clientList;
    private Socket clientSocket;
    private Scanner in;
    private PrintWriter out;

    public ClientHandler(Socket clientSocket, Collection<ClientHandler> clientList)
    {
        this.clientSocket = clientSocket;
        this.clientList = clientList;
        try
        {
            this.in = new Scanner(clientSocket.getInputStream());
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);

        } catch (IOException ex)
        {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run()
    {

        String[] strings = null;
        String input;
        while ((input = in.nextLine()) != null)
        {
            strings = input.split(":");
            
            
                
            for (ClientHandler clientHandler : clientList)
            {
                if (strings[1].equalsIgnoreCase(clientHandler.getName()))
                {
                    clientSocket.close();
                    return;
                }
            }
            
            

        }

    }

    public String getName()
    {
        return this.name;
    }

}
