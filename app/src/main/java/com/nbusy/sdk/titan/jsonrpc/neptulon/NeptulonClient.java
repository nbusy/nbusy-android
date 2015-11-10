package com.nbusy.sdk.titan.jsonrpc.neptulon;

import android.net.SSLCertificateSocketFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

/**
 * Neptulon client implementation: https://github.com/neptulon/neptulon
 */
public class NeptulonClient implements Neptulon {
    private SSLSocket socket;

    public void connect(String pemEncodedCaCert, String pemEncodedClientCert) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        SSLCertificateSocketFactory factory;
        try (InputStream caCertStream = new ByteArrayInputStream(pemEncodedCaCert.getBytes());
             InputStream clientCertStream = new ByteArrayInputStream(pemEncodedClientCert.getBytes())) {
            factory = getSocketFactory(caCertStream, clientCertStream);
        }

        socket = (SSLSocket) factory.createSocket("localhost", 8081);
    }

    public void close() throws IOException {
        socket.close();
    }

    private SSLCertificateSocketFactory getSocketFactory(InputStream caCertStream, InputStream clientCertStream) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        SSLCertificateSocketFactory factory = (SSLCertificateSocketFactory) SSLCertificateSocketFactory.getDefault(60 * 1000, null);

        // set CA cert to trust
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate ca = cf.generateCertificate(caCertStream);
        KeyStore caKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        caKeyStore.load(null, null);
        caKeyStore.setCertificateEntry("ca", ca);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(caKeyStore);
        factory.setTrustManagers(tmf.getTrustManagers());

        // set client cert
        Certificate cl = cf.generateCertificate(clientCertStream);
        KeyStore clientKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        clientKeyStore.load(null, null);
        clientKeyStore.setKeyEntry("client", null, new Certificate[]{cl});
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientKeyStore, new char[]{});

        return factory;
    }
}
