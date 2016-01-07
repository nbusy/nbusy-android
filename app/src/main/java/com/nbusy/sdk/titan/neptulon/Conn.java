package com.nbusy.sdk.titan.neptulon;

/**
 * Neptulon connection interface: https://github.com/neptulon/neptulon
 */
public interface Conn {
    /**
     * Enables Transport Layer Security for the connection.
     * All certificates/private keys are in PEM encoded X.509 format.
     * @param ca Optional CA certificate to be used for verifying the server certificate. Useful for using self-signed server certificates.
     * @param clientCert Optional certificate/private key pair for TLS client certificate authentication.
     * @param clientCertKey Optional certificate/private key pair for TLS client certificate authentication.
     */
    void useTLS(byte[] ca, byte[] clientCert, byte[] clientCertKey);
}
