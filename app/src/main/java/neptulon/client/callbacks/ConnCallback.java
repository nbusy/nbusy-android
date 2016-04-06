package neptulon.client.callbacks;

/**
 * Callback for connection event.
 */
public interface ConnCallback {
    void connected(String reason);
    void disconnected(String reason);
}
