package neptulon.client;

/**
 * Handler for connection event.
 */
public interface ConnHandler {
    void connected();
    void disconnected(String reason);
}
