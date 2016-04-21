package titan.client;

import titan.client.callbacks.ConnCallbacks;
import titan.client.callbacks.EchoCallback;
import titan.client.callbacks.AuthCallback;
import titan.client.callbacks.SendMsgsCallback;
import titan.client.messages.Message;

/**
 * Titan client interface: https://github.com/titan-x/titan
 */
public interface Client {
    void connect(ConnCallbacks cbs);

    boolean isConnected();

    boolean jwtAuth(String token, AuthCallback cb);

    boolean googleAuth(String token, AuthCallback cb);

    boolean echo(String msg, EchoCallback cb);

    boolean sendMessages(SendMsgsCallback cb, Message... msgs);

    void close();
}
