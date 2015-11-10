package com.nbusy.sdk.titan.jsonrpc.neptulon;

import android.net.SSLCertificateSocketFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

/**
 * Neptulon client implementation: https://github.com/neptulon/neptulon
 */
public class NeptulonClient implements Neptulon {
    private SSLSocket socket;

    public void connect(String pemEncodedCaCert) {
        InputStream caCertStream = new ByteArrayInputStream(pemEncodedCaCert.getBytes());
        SSLCertificateSocketFactory factory = getSocketFactory(caCertStream);

        socket = (SSLSocket)factory.createSocket("localhost", 8081);
    }

    private SSLCertificateSocketFactory getSocketFactory(InputStream caCertStream) {
        SSLCertificateSocketFactory factory = (SSLCertificateSocketFactory)SSLCertificateSocketFactory.getDefault(60 * 1000, null);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        try {
            Certificate ca = cf.generateCertificate(caCertStream);
        } finally {
            caCertStream.close();
        }

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);
        factory.setTrustManagers(tmf.getTrustManagers());

        return factory;
    }
}
