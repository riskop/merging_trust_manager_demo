# Demo application to show two strategies for loading truststore *on top of* the system default trust store

One strategy is to merge the x509 certs from the system's default truststore
and the additional trustore, and initialize a new truststore with this
merged set of x509 certs.

The other strategy is to use a "delegating" TrustManager,
where the trust manager first checks the system's default trustsore
and if an exception is thrown then tries a secondary Trustmanager
which is initialized with the additional certs.

If you just run the demo it will use the "delegating" strategy.

```
mvn spring-boot:run

2025-11-20T15:34:09.444+01:00  INFO 3121631 --- [SslContextBuilderDemo] [           main] c.e.S.TrustManagerDemoApplication        : Starting TrustManagerDemoApplication using Java 21.0.1 with PID 3121631 (/home/riskop/IdeaProjects/merging_trust_manager_demo/target/classes started by riskop in /home/riskop/IdeaProjects/merging_trust_manager_demo)
2025-11-20T15:34:09.446+01:00  INFO 3121631 --- [SslContextBuilderDemo] [           main] c.e.S.TrustManagerDemoApplication        : No active profile set, falling back to 1 default profile: "default"
2025-11-20T15:34:09.670+01:00  INFO 3121631 --- [SslContextBuilderDemo] [           main] c.e.S.TrustManagerDemoApplication        : Started TrustManagerDemoApplication in 0.426 seconds (process running for 0.57)
*** using DELEGATING trustmanager ***
*** site cert found in main trustmanager ***
*** site cert found in main trustmanager ***
google.com content length: 18311
*** site cert found in additional trustmanager ***
untrusted-root.badssl.com content length: 600
*** site cert found in additional trustmanager ***
teszt.kv.gov.hu content length: 4575
```

If you run it with "merged" argument then it will use the "merging" strategy:

mvn spring-boot:run -Dspring-boot.run.arguments=merged

```
2025-11-20T15:32:16.281+01:00  INFO 3121435 --- [SslContextBuilderDemo] [           main] c.e.S.TrustManagerDemoApplication        : Starting TrustManagerDemoApplication using Java 21.0.1 with PID 3121435 (/home/riskop/IdeaProjects/merging_trust_manager_demo/target/classes started by riskop in /home/riskop/IdeaProjects/merging_trust_manager_demo)
2025-11-20T15:32:16.283+01:00  INFO 3121435 --- [SslContextBuilderDemo] [           main] c.e.S.TrustManagerDemoApplication        : No active profile set, falling back to 1 default profile: "default"
2025-11-20T15:32:16.514+01:00  INFO 3121435 --- [SslContextBuilderDemo] [           main] c.e.S.TrustManagerDemoApplication        : Started TrustManagerDemoApplication in 0.442 seconds (process running for 0.574)
*** using MERGING trustmanager ***
*** merged truststore size after merging system's default trusted certs 136 ***
*** merged truststore size after merging additional trusted certs 138 ***
google.com content length: 18173
untrusted-root.badssl.com content length: 600
teszt.kv.gov.hu content length: 4575
```