package server;

public class ServerInit {

    public static void main(String[] args)
    {
        Server server = new Server();
        server.startServer("127.0.0.1", 8081);
    }

}
