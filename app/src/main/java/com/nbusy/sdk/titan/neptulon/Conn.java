package com.nbusy.sdk.titan.neptulon;

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
    void middleware();

    /**
     * Connects to the given WebSocket server.
     */
    void connect();

    /**
     * Returns the remote network address.
     */
    void remoteAddr();

    /**
     * Sends a JSON-RPC request through the connection with an auto generated request ID.
     *
     * @param resHandler is called when a response is returned.
     */
    void sendRequest(int resHandler);

    /**
     * Sends a JSON-RPC request through the connection, with array params and auto generated request ID.
     *
     * @param resHandler is called when a response is returned.
     */
    void sendRequestArr(int resHandler);

    /**
     * Closes the connection.
     */
    void close();
}
