package com.example.SslContextBuilderDemo;

import org.apache.hc.client5.http.fluent.Request;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;

@SpringBootApplication
public class TrustManagerDemoApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(TrustManagerDemoApplication.class, args);
	}

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public void run(String... args) throws Exception {
        String additionalTruststoreFile = "additional_CAs_truststore.jks";
        char[] password = "changeit".toCharArray();
        X509TrustManager trustManager;
        if(args.length==1 && "merged".equals(args[0])) {
            System.out.println("*** using MERGING trustmanager ***");
            trustManager = MergedTrustManagerFactory.getTrustManagerWithMergedTruststore(additionalTruststoreFile, password);
        }
        else {
            System.out.println("*** using DELEGATING trustmanager ***");
            trustManager = DelegatingTrustManagerFactory.getTrustManagerDelegatingToAdditionalTruststore(additionalTruststoreFile, password);
        }

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(
                null,
                new TrustManager[]{
                    trustManager
                },
                null
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
