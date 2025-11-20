package com.example.SslContextBuilderDemo;

import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class MergedTrustManagerFactory {

    public static X509TrustManager getTrustManagerWithMergedTruststore(String trustStoreFileName, char[] trustStorePassword) {
        KeyStore trustStoreWithAdditionalCAs = TrustStoreUtil.getKeyStoreFromFile(trustStoreFileName, trustStorePassword);
        try {
            final TrustManagerFactory javaDefaultTrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            javaDefaultTrustManagerFactory.init((KeyStore) null);
            final X509TrustManager javaDefaultTrustManager = (X509TrustManager) javaDefaultTrustManagerFactory.getTrustManagers()[0];

            final TrustManagerFactory additionalCAsTrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            additionalCAsTrustManagerFactory.init(trustStoreWithAdditionalCAs);
            final X509TrustManager additionalCAsTrustManager = (X509TrustManager) additionalCAsTrustManagerFactory.getTrustManagers()[0];

            KeyStore ks = TrustStoreUtil.createEmptyKeyStoreInitialized();
            for(X509Certificate x509Certificate : javaDefaultTrustManager.getAcceptedIssuers()) {
                ks.setCertificateEntry(x509Certificate.getSubjectX500Principal().getName(), x509Certificate);
            }
            System.out.println("*** merged truststore size after merging system's default trusted certs " + ks.size() + " ***");
            for(X509Certificate x509Certificate : additionalCAsTrustManager.getAcceptedIssuers()) {
                ks.setCertificateEntry(x509Certificate.getSubjectX500Principal().getName(), x509Certificate);
            }
            System.out.println("*** merged truststore size after merging additional trusted certs " + ks.size() + " ***");

            final TrustManagerFactory mergedTrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            mergedTrustManagerFactory.init(ks);
            return (X509TrustManager)mergedTrustManagerFactory.getTrustManagers()[0];
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

}
