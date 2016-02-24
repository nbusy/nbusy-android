package titan.client;

import neptulon.client.callbacks.ConnCallback;
import titan.client.callbacks.JwtAuthCallback;
import titan.client.callbacks.SendMsgCallback;
import titan.client.messages.Message;

/**
 * Titan client interface: https://github.com/titan-x/titan
 */
public interface Client {
    /**
     * Connects to the given Titan server.
     *
     * @param handler Handler for connection/disconnection events.
     */
    void connect(ConnCallback handler);

    void jwtAuth(String token, JwtAuthCallback cb);

    void sendMessages(Message[] msgs, SendMsgCallback cb);

    void close();
}
