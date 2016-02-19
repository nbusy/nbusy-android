package titan.client;

import titan.client.callbacks.Callback;

/**
 * Titan client interface: https://github.com/titan-x/titan
 */
public interface Client {
    void connect(); // todo: add callbacks for success/fail (or use events like okwebsockets?)

    void jwtAuth(String token, Callback success, Callback fail);

    void sendMessage(String to, String msg, Callback sentToServer, Callback delivered);

    void close();
}
