package com.nbusy.sdk.titan.jsonrpc.neptulon;

import android.net.SSLCertificateSocketFactory;
import android.net.SSLSessionCache;

import java.io.IOException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;

/**
 * Neptulon client implementation: https://github.com/neptulon/neptulon
 */
public class NeptulonClient implements Neptulon {
    private final SSLSocket socket;

    public NeptulonClient() throws IOException {
        // todo: provide session cache instance
        // SSLContext.getDefault() instead if it returns SSLCertificateSocketFactory?
        SSLCertificateSocketFactory factory = (SSLCertificateSocketFactory)SSLCertificateSocketFactory.getDefault(60 * 1000, null);
        socket = (SSLSocket)factory.createSocket("localhost", 8081);
    }
}
