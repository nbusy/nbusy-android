package titan.client;

import titan.client.callbacks.ConnCallbacks;
import titan.client.callbacks.EchoCallback;
import titan.client.callbacks.GoogleAuthCallback;
import titan.client.callbacks.JWTAuthCallback;
import titan.client.callbacks.SendMsgsCallback;
import titan.client.messages.MsgMessage;

/**
 * Titan client interface: https://github.com/titan-x/titan
 */
public interface Client {
    void connect(ConnCallbacks cbs);

    boolean isConnected();

    boolean googleAuth(String token, GoogleAuthCallback cb);

    boolean jwtAuth(String token, JWTAuthCallback cb);

    boolean echo(String msg, EchoCallback cb);

    boolean sendMessages(SendMsgsCallback cb, MsgMessage... msgs);

    void close();
}
