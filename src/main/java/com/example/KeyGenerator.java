package com.example;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) throws JOSEException, IOException {
        final RSAKey key = new RSAKeyGenerator(4096).generate();
        final PrivateKey privateKey = key.toPrivateKey();
        final PublicKey publicKey = key.toPublicKey();

        final Path privateKeyPath = Paths.get("sign.key");
        final Path publicKeyPath = Paths.get("verify.key");

        final byte[] privateKeyBytes = Base64.getMimeEncoder().encode(
                privateKey.getEncoded()
        );
        Files.write(privateKeyPath, privateKeyBytes);
        final byte[] publicKeyBytes = Base64.getMimeEncoder().encode(
                publicKey.getEncoded()
        );
        Files.write(publicKeyPath, publicKeyBytes);
    }
}
