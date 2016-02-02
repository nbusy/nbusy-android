package titan.client;

import titan.client.callbacks.Callback;

/**
 * Titan client interface: https://github.com/titan-x/titan
 */
public interface Client {
    void connect();

    boolean isConnected();

    // todo: will the callback throw exception when the service stops?
    void sendMessage(String to, String msg, Callback sentToServerCallback, Callback deliveredCallback);

    void close();
}
