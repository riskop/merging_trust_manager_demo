package com.example.SslContextBuilderDemo;

import org.apache.hc.client5.http.fluent.Request;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@SpringBootApplication
public class MergingTrustManagerDemoApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(MergingTrustManagerDemoApplication.class, args);
	}

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public void run(String... args) throws Exception {

        final TrustManagerFactory javaDefaultTrustManager = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        javaDefaultTrustManager.init((KeyStore) null);

        final TrustManagerFactory additionalCaTrustManager = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore ks = KeyStore.getInstance("JKS");
        InputStream is = new FileInputStream("additional_CAs_truststore.jks");
        ks.load(is, "changeit".toCharArray());
        is.close();
        additionalCaTrustManager.init(ks);

        for(X509Certificate x509Certificate : ((X509TrustManager)javaDefaultTrustManager.getTrustManagers()[0]).getAcceptedIssuers()) {
            System.out.println("Accepted issuer from java default trust manager: " + x509Certificate.getIssuerX500Principal());
        }
        for(X509Certificate x509Certificate : ((X509TrustManager)additionalCaTrustManager.getTrustManagers()[0]).getAcceptedIssuers()) {
            System.out.println("Accepted issuer from additional trust manages: " + x509Certificate.getIssuerX500Principal());
        }

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(
                null,
                new TrustManager[]{
                        new TrustManagerDelegate(
                                (X509TrustManager)additionalCaTrustManager.getTrustManagers()[0],
                                (X509TrustManager)javaDefaultTrustManager.getTrustManagers()[0]
                        )
                },
                secureRandom
        );
        SSLContext.setDefault(sslContext);

        {
            String content = Request.get("https://google.com").execute().returnContent().asString();
            System.out.println("google.com content length: "  + content.length());
        }

        {
            String content = Request.get("https://untrusted-root.badssl.com/").execute().returnContent().asString();
            System.out.println("untrusted-root.badssl.com content length: " + content.length());
        }

        {
            String content = Request.get("https://teszt.kv.gov.hu/").execute().returnContent().asString();
            System.out.println("teszt.kv.gov.hu content length: " + content.length());
        }

    }
}
