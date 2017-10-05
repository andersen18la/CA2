package server;

public class ServerInit {

    public static void main(String[] args)
    {
        Server server = new Server();
        server.startServer("0.0.0.0", 8081);
    }

}
