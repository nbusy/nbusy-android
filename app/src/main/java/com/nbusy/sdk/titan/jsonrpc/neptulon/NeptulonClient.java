package com.nbusy.sdk.titan.jsonrpc.neptulon;

import android.net.SSLCertificateSocketFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

/**
 * Neptulon client implementation: https://github.com/neptulon/neptulon
 */
public class NeptulonClient implements Neptulon {
    private SSLSocket socket;

    public void connect(String pemEncodedCaCert) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        InputStream caCertStream = new ByteArrayInputStream(pemEncodedCaCert.getBytes());
        SSLCertificateSocketFactory factory = getSocketFactory(caCertStream);

        socket = (SSLSocket)factory.createSocket("localhost", 8081);
    }

    public void close() throws IOException {
        socket.close();
    }

    private SSLCertificateSocketFactory getSocketFactory(InputStream caCertStream) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException {
        SSLCertificateSocketFactory factory = (SSLCertificateSocketFactory)SSLCertificateSocketFactory.getDefault(60 * 1000, null);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        Certificate ca;
        try {
            ca = cf.generateCertificate(caCertStream);
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
