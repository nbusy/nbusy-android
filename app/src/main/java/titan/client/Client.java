package titan.client;

/**
 * Titan client interface: https://github.com/titan-x/titan
 */
public interface Client {
    void connect();

    void send(String to, Message msg);

    void close();
}
