package com.example.SslContextBuilderDemo;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Idea taken from here:
 *
 * https://web.archive.org/web/20170907065857/http://blog.novoj.net:80/2016/02/29/how-to-make-apache-httpclient-trust-lets-encrypt-certificate-authority/
 *
 * https://stackoverflow.com/questions/46529633/how-to-add-ca-certificate-to-cacerts-store-so-that-it-works-as-expected
 *
 */
public class TrustManagerDelegate implements X509TrustManager {
    private final X509TrustManager mainTrustManager;
    private final X509TrustManager fallbackTrustManager;

    public TrustManagerDelegate(X509TrustManager mainTrustManager, X509TrustManager fallbackTrustManager) {
        this.mainTrustManager = mainTrustManager;
        this.fallbackTrustManager = fallbackTrustManager;
    }

    @Override
    public void checkClientTrusted(final X509Certificate[] x509Certificates, final String authType) throws CertificateException {
        try {
            mainTrustManager.checkClientTrusted(x509Certificates, authType);
        } catch(CertificateException ignored) {
            this.fallbackTrustManager.checkClientTrusted(x509Certificates, authType);
        }
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] x509Certificates, final String authType) throws CertificateException {
        try {
            mainTrustManager.checkServerTrusted(x509Certificates, authType);
        } catch(CertificateException ignored) {
            this.fallbackTrustManager.checkServerTrusted(x509Certificates, authType);
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return this.fallbackTrustManager.getAcceptedIssuers();
    }
}
