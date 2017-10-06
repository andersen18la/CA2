/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.IOException;

/**
 *
 * @author marik
 */
public class ChatTTest {

    public static void main(String[] args) throws IOException, InterruptedException
    {
        for (int i = 0; i < 10; i++)
        {

            ChatClient client = new ChatClient();
            client.addObserver((msg) ->
            {
                System.out.println(msg);
            });

            client.connect("127.0.0.1", 8081);
            client.send("LOGIN:BOB" + i);
            client.send("MSG:*:Hello");
            client.send("MSG:*:Hello World");
            client.send("MSG:*:Hello Wonderfull World");
            Thread.sleep(300);
            client.closeConnection();

            System.out.println("DONE");
        }

    }

}
