package titan.client;

import neptulon.client.ConnHandler;
import titan.client.callbacks.JwtAuthCallback;
import titan.client.callbacks.SendMessageCallback;

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

    void jwtAuth(String token, JwtAuthCallback cb);

    void sendMessage(String to, String msg, SendMessageCallback cb);

    void close();
}
