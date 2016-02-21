package neptulon.client;

import neptulon.client.callbacks.ConnCallback;
import neptulon.client.callbacks.ResCallback;

/**
 * Neptulon connection interface: https://github.com/neptulon/neptulon
 */
public interface Conn {
    /**
     * Enables Transport Layer Security for the connection.
     * All certificates/private keys are in PEM encoded X.509 format.
     *
     * @param ca            Optional CA certificate to be used for verifying the server certificate. Useful for using self-signed server certificates.
     * @param clientCert    Optional certificate/private key pair for TLS client certificate authentication.
     * @param clientCertKey Optional certificate/private key pair for TLS client certificate authentication.
     */
    void useTLS(byte[] ca, byte[] clientCert, byte[] clientCertKey);

    /**
     * Sets the read/write deadlines for the connection, in seconds.
     * Default value for read/write deadline is 300 seconds.
     */
    void setDeadline(int seconds);

    /**
     * Registers middleware to handle incoming request messages.
     */
    void middleware(Middleware mw);

    /**
     * Connects to the given Neptulon server.
     *
     * @param handler Handler for connection/disconnection events.
     */
    void connect(ConnCallback handler);

    /**
     * Returns the remote network address.
     */
    void remoteAddr();

    /**
     * Sends a JSON-RPC request through the connection with an auto generated request ID.
     */
    <T> void sendRequest(String method, T params, ResCallback cb);

    /**
     * Sends a JSON-RPC request through the connection, with array params and auto generated request ID.
     */
    void sendRequestArr(String method, ResCallback cb, Object... params);

    /**
     * Closes the connection.
     */
    void close();
}
