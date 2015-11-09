package com.nbusy.sdk.titan.jsonrpc.neptulon;

import android.net.SSLCertificateSocketFactory;

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
    private final SSLSocket socket;

    public NeptulonClient(InputStream caCert) throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException {
        // todo: provide session cache instance and limit ciphers
        SSLCertificateSocketFactory factory = (SSLCertificateSocketFactory)SSLCertificateSocketFactory.getDefault(60 * 1000, null);

        // trust given CA certificate
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate ca = cf.generateCertificate(caCert);
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);
        factory.setTrustManagers(tmf.getTrustManagers());

        // create socket but don't connect yet
        socket = (SSLSocket)factory.createSocket("localhost", 8081);
    }
}
