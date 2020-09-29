package com.esteel.rest.autoconfigure.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Data
@Component
@AllArgsConstructor
public class KeyConfig {

//  static final String VERIFIER_KEY_ID = new String(Base64.encode(KeyGenerators.secureRandom(32).generateKey()));

  private RSAPublicKey publicKey;

  private RSAPrivateKey privateKey;


  public RSAPublicKey getVerifierKey() {
    return getPublicKey();
  }

  public RSAPrivateKey getSignerKey() {
    return getPrivateKey();
  }

//  private KeyPair getKeyPair() {
//    return KEY_STORE_KEY_FACTORY.getKeyPair(KEY_ALIAS);
//  }
}
