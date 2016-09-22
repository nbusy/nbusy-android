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
     * Registers a middleware to handle incoming requests on a specific route (JSON-RPC request method).
     * This method simply registers given routes on a private {Router} instance.
     */
    void handleRequest(String route, Middleware mw);

    /**
     * Connects to the given Neptulon server.
     *
     * @param cb Callback for connection/disconnection events.
     */
    void connect(ConnCallback cb);

    /**
     * Whether the connection is established.
     */
    boolean isConnected();

    /**
     * Whether the connection has ongoing requests.
     */
    boolean haveOngoingRequests();

    /**
     * Returns the remote network address.
     */
    String remoteAddr();

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
