package titan.client;

/**
 * Titan client interface: https://github.com/titan-x/titan
 */
public interface Client {
    void connect();

    // todo: will the callback throw exception when the service stops?
    void sendMessage(String to, String msg, Callback sentToServerCallback, Callback deliveredCallback);

    void close();
}
