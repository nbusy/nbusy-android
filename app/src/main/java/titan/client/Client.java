package titan.client;

import titan.client.callbacks.ConnCallbacks;
import titan.client.callbacks.JwtAuthCallback;
import titan.client.callbacks.SendMsgCallback;
import titan.client.messages.Message;

/**
 * Titan client interface: https://github.com/titan-x/titan
 */
public interface Client {
    void connect(ConnCallbacks cbs);

    void jwtAuth(String token, JwtAuthCallback cb);

    void sendMessages(Message[] msgs, SendMsgCallback cb);

    void close();
}