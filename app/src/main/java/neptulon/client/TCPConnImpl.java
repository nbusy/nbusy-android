package neptulon.client;
//
//import android.net.SSLCertificateSocketFactory;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.security.KeyStore;
//import java.security.KeyStoreException;
//import java.security.NoSuchAlgorithmException;
//import java.security.UnrecoverableKeyException;
//import java.security.cert.Certificate;
//import java.security.cert.CertificateException;
//import java.security.cert.CertificateFactory;
//import java.security.cert.X509Certificate;
//
//import javax.net.ssl.KeyManagerFactory;
//import javax.net.ssl.SSLSocket;
//import javax.net.ssl.TrustManagerFactory;
//
///**
// * Neptulon TCP connection implementation: https://github.com/neptulon/neptulon/tree/raw-tcp
// */
//public class TCPConnImpl {
//    private final SSLCertificateSocketFactory factory;
//    private SSLSocket socket;
//
//    public TCPConnImpl(String pemEncodedCaCert, String pemEncodedClientCert, byte[] privateKey) throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
//        try (InputStream caCertStream = new ByteArrayInputStream(pemEncodedCaCert.getBytes());
//             InputStream clientCertStream = new ByteArrayInputStream(pemEncodedClientCert.getBytes())) {
//            factory = getSocketFactory(caCertStream, clientCertStream, privateKey);
//        }
//    }
//
//    public void connect() throws IOException {
//        socket = (SSLSocket) factory.createSocket("localhost", 8081);
//    }
//
//    public void close() throws IOException {
//        socket.close();
//    }
//
//    private SSLCertificateSocketFactory getSocketFactory(InputStream caCertStream, InputStream clientCertStream, byte[] privateKey) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
//        SSLCertificateSocketFactory factory = (SSLCertificateSocketFactory) SSLCertificateSocketFactory.getDefault(60 * 1000, null);
//
//        // set CA cert to trust
//        CertificateFactory cf = CertificateFactory.getInstance("X.509");
//        X509Certificate ca = (X509Certificate) cf.generateCertificate(caCertStream);
//        KeyStore caKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//        caKeyStore.load(null, null);
//        caKeyStore.setCertificateEntry(ca.getSubjectX500Principal().getName(), ca);
//        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//        tmf.init(caKeyStore);
//        factory.setTrustManagers(tmf.getTrustManagers());
//
//        // set client cert
//        X509Certificate cl = (X509Certificate) cf.generateCertificate(clientCertStream);
//        KeyStore clientKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//        clientKeyStore.load(null, null);
//        clientKeyStore.setKeyEntry(cl.getSubjectX500Principal().getName(), privateKey, new Certificate[]{cl});
//        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//        kmf.init(clientKeyStore, new char[]{});
//
//        // todo: alternative approach if above doesn't work
////        keyStore = KeyStore.getInstance("PKCS12");
////        fis = new FileInputStream(certificateFile);
////        keyStore.load(fis, clientCertPassword.toCharArray());
//
//        // more options can be found at: http://stackoverflow.com/questions/23103174/does-okhttp-support-accepting-self-signed-ssl-certs/24401795#24401795
//
//        return factory;
//    }
//}
