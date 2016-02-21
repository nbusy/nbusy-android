package neptulon.client.callbacks;

/**
 * Callback for connection event.
 */
public interface ConnCallback {
    void connected();
    void disconnected(String reason);
}
