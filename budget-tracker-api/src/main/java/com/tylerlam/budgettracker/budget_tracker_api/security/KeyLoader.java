package com.tylerlam.budgettracker.budget_tracker_api.security;

import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * KeyLoader is responsible for loading RSA public and private keys from PEM files
 * and converting them into Java PrivateKey and PublicKey objects.
 *
 *
 * The keys are expected to be in standard PEM format:
 * 
 *   Private key: PKCS#8 format ("-----BEGIN PRIVATE KEY-----")
 *   Public key: X.509 format ("-----BEGIN PUBLIC KEY-----")
 * 
 * This class uses Spring's ResourceLoader so keys can be loaded from
 * different locations (classpath, filesystem, mounted secrets, etc.).
 *
 * Typical usage:
 *   Private key → sign JWTs (RS256)
 *   Public key → verify JWTs
 */
@Component
@RequiredArgsConstructor
public class KeyLoader {

    private final ResourceLoader resourceLoader;

    /**
     * Loads an RSA private key from a PEM-formatted file and converts it
     * into a PrivateKey instance usable by Java cryptography APIs.
     *
     * The method performs the following steps:
     *   Loads the key file using Spring's ResourceLoader
     *   Removes PEM headers, footers, and whitespace
     *   Base64-decodes the remaining content into DER bytes
     *   Wraps the bytes in a PKCS8EncodedKeySpec
     *   Uses KeyFactory to generate a PrivateKey
     *
     * @param keyPath the resource path to the private key file
     *                (e.g. "classpath:keys/private.pem" or "file:/secrets/private.pem")
     * @return the loaded PrivateKey
     *
     * @throws IOException if the key file cannot be read
     * @throws NoSuchAlgorithmException if the RSA algorithm is not available
     * @throws InvalidKeySpecException if the key contents are invalid or malformed
     */
    public PrivateKey loadPrivateKey(String keyPath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Resource resource = resourceLoader.getResource(keyPath);
        String key = new String(Files.readAllBytes(resource.getFile().toPath()));

        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        
        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * Loads an RSA public key from a PEM-formatted file and converts it
     * into a PublicKey instance usable by Java cryptography APIs.
     *
     * This method mirrors loadPrivateKey(String) but uses
     * X509EncodedKeySpec, which is the standard format for public keys.
     *
     * @param keyPath the resource path to the public key file
     *                (e.g. "classpath:keys/public.pem" or "file:/secrets/public.pem")
     * @return the loaded PublicKey
     *
     * @throws IOException if the key file cannot be read
     * @throws NoSuchAlgorithmException if the RSA algorithm is not available
     * @throws InvalidKeySpecException if the key contents are invalid or malformed
     */
    public PublicKey loadPublicKey(String keyPath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Resource resource = resourceLoader.getResource(keyPath);
        String key = new String(Files.readAllBytes(resource.getFile().toPath()));

        String publicKeyPEM = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        
        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return keyFactory.generatePublic(keySpec);
    }
}
