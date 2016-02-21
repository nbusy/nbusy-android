package titan.client;

import neptulon.client.ConnHandler;
import titan.client.callbacks.Callback;

/**
 * Titan client interface: https://github.com/titan-x/titan
 */
public interface Client {
    /**
     * Connects to the given Titan server.
     *
     * @param handler Handler for connection/disconnection events.
     */
    void connect(ConnHandler handler);

    void jwtAuth(String token, Callback success, Callback fail);

    void sendMessage(String to, String msg, Callback sentToServer, Callback delivered);

    void close();
}
