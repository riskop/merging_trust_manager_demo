echo 'yes' | keytool -keystore additional_CAs_truststore.jks -importcert -alias badssluntrustedroot -storepass changeit -file BadSSL_Untrusted_Root_Certificate_Authority.pem
echo 'yes' | keytool -keystore additional_CAs_truststore.jks -importcert -alias tesztkvgovhu -storepass changeit -file teszt_kv_gov_hu.pem
