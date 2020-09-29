package com.esteel.rest.autoconfigure.security;

import com.esteel.rest.properties.SecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * @author liujie
 * @version 1.0.0
 * @ClassName AuthorizationServerConfig.java
 * @Description TODO
 * @createTime 2019年12月10日 14:27
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
@EnableAuthorizationServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

  @Autowired
  SecurityProperties properties;

  @Value("${auth.publicKey}")
  private RSAPublicKey publicKey;

  @Value("${auth.privateKey}")
  private RSAPrivateKey privateKey;

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @Autowired
  private JwtAccessTokenConverter jwtAccessTokenConverter;

//  @Autowired
//  private AuthenticationManager authenticationManager;


  @Bean
  public AuthorizationServerTokenServices authorizationServerTokenServices(){
    DefaultTokenServices tokenServices = new DefaultTokenServices();
    tokenServices.setTokenEnhancer(jwtAccessTokenConverter);
    tokenServices.setTokenStore(tokenStore());
    if(Objects.nonNull(properties.getExpireSeconds())&&properties.getExpireSeconds()>0) {
      tokenServices.setAccessTokenValiditySeconds(properties.getExpireSeconds());
    }
    return tokenServices;
  }

  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
    endpoints
//        .authenticationManager(this.authenticationManager)
        .tokenStore(tokenStore())
        .accessTokenConverter(accessTokenConverter());
  }

  @Bean
  public TokenStore tokenStore() {
    TokenStore tokenStore ;

//    log.debug("==========> MultiSignIn:{}",properties.getMultiSignIn());
    if(properties.getMultiSignIn()){
      tokenStore = new JwtTokenStore(accessTokenConverter());
    }else {
      JwtRedisTokenStore jwtRedisTokenStore = new JwtRedisTokenStore(accessTokenConverter());
      jwtRedisTokenStore.setStringRedisTemplate(stringRedisTemplate);
      tokenStore = jwtRedisTokenStore;
    }
    return tokenStore;
  }

  @Bean
  public KeyConfig keyConfig() {
    KeyConfig keyConfig = new KeyConfig(publicKey,privateKey);
    return keyConfig;
  }

  @Bean
  public JwtAccessTokenConverter accessTokenConverter() {
    KeyConfig keyConfig = keyConfig();
    final RsaSigner signer = new RsaSigner(keyConfig.getSignerKey());

    JwtAccessTokenConverter converter = new JwtAccessTokenConverter() {
      private JsonParser objectMapper = JsonParserFactory.create();

      @Override
      protected String encode(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        String content;
        try {
          content = this.objectMapper.formatMap(getAccessTokenConverter().convertAccessToken(accessToken, authentication));
        } catch (Exception ex) {
          throw new IllegalStateException("Cannot convert access token to JSON", ex);
        }
        Map<String, String> headers = new HashMap<>();
//        headers.put("kid", KeyConfig.VERIFIER_KEY_ID);
        String token = JwtHelper.encode(content, signer, headers).getEncoded();
        return token;
      }
    };
    converter.setSigner(signer);
    converter.setVerifier(new RsaVerifier(keyConfig.getVerifierKey()));
    return converter;
  }




}

