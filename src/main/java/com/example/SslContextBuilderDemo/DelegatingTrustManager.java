package com.example.SslContextBuilderDemo;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Idea taken from here:
 *
 * https://web.archive.org/web/20170907065857/http://blog.novoj.net:80/2016/02/29/how-to-make-apache-httpclient-trust-lets-encrypt-certificate-authority/
 *
 * https://stackoverflow.com/questions/46529633/how-to-add-ca-certificate-to-cacerts-store-so-that-it-works-as-expected
 *
 */
public class DelegatingTrustManager implements X509TrustManager {
    private final X509TrustManager mainTrustManager;
    private final X509TrustManager additionalTrustManager;

    public DelegatingTrustManager(X509TrustManager mainTrustManager, X509TrustManager additionalTrustManager) {
        this.mainTrustManager = mainTrustManager;
        this.additionalTrustManager = additionalTrustManager;
    }

    @Override
    public void checkClientTrusted(final X509Certificate[] x509Certificates, final String authType) throws CertificateException {
        try {
            mainTrustManager.checkClientTrusted(x509Certificates, authType);
        } catch(CertificateException ignored) {
            this.additionalTrustManager.checkClientTrusted(x509Certificates, authType);
        }
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] x509Certificates, final String authType) throws CertificateException {
        try {
            mainTrustManager.checkServerTrusted(x509Certificates, authType);
            System.out.println("*** site cert found in main trustmanager ***");
        } catch(CertificateException ignored) {
            this.additionalTrustManager.checkServerTrusted(x509Certificates, authType);
            System.out.println("*** site cert found in additional trustmanager ***");
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        X509Certificate[] acceptedIssuersFromMainTrustManager = this.mainTrustManager.getAcceptedIssuers();
        X509Certificate[] acceptedIssuersFromFallbackTrustManager = this.additionalTrustManager.getAcceptedIssuers();
        List<X509Certificate> merged = new ArrayList<>();
        merged.addAll(Arrays.asList(acceptedIssuersFromMainTrustManager));
        merged.addAll(Arrays.asList(acceptedIssuersFromFallbackTrustManager));
        return merged.toArray(new X509Certificate[0]);
    }
}
