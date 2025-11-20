package com.example.SslContextBuilderDemo;

import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class DelegatingTrustManagerFactory {

    public static X509TrustManager getTrustManagerDelegatingToAdditionalTruststore(String trustStoreFileName, char[] trustStorePassword) {
        final TrustManagerFactory javaDefaultTrustManager;
        try {
            javaDefaultTrustManager = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            javaDefaultTrustManager.init((KeyStore) null);
            final TrustManagerFactory additionalCaTrustManager = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            additionalCaTrustManager.init(TrustStoreUtil.getKeyStoreFromFile(trustStoreFileName, trustStorePassword));
            X509TrustManager delegatingTrustManager = new DelegatingTrustManager(
                    (X509TrustManager)javaDefaultTrustManager.getTrustManagers()[0],
                    (X509TrustManager)additionalCaTrustManager.getTrustManagers()[0]
            );
            return delegatingTrustManager;
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

}
