/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

/**
 *
 * @author marik
 */
public interface IObserver {

    void messageReady(String msg);

    void updateClientList(String msg);

    void connectionLost(String msg);

    void connectedToServer(String msg);

    void loggedIn(String msg);

    void showOwnPrivateMessage(String msg);
    
    void clientDisconnected(String msg);
    
    void clientConnected(String msg);
}
