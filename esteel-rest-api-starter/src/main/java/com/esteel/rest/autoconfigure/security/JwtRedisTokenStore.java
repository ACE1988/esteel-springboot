package com.esteel.rest.autoconfigure.security;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import static com.esteel.rest.security.SystemAuthority.AUTHORITY_ESTEEL;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@Setter
@Getter
public class JwtRedisTokenStore extends JwtTokenStore {


  private StringRedisTemplate stringRedisTemplate;

  /**
   * Create a JwtTokenStore with this token enhancer (should be shared with the DefaultTokenServices
   * if used).
   */
  public JwtRedisTokenStore(JwtAccessTokenConverter jwtTokenEnhancer) {
    super(jwtTokenEnhancer);
  }

  private String getRedisKey(OAuth2Authentication authentication ){

    String redisSuffx = "";
    if(CollectionUtils.isNotEmpty(authentication.getAuthorities())){
      for(GrantedAuthority authority:authentication.getAuthorities()){
        if(StringUtils.equalsIgnoreCase(AUTHORITY_ESTEEL.getAuthority(),authority.getAuthority())){
          redisSuffx = AUTHORITY_ESTEEL.getAuthority();
        }
      }
    }
    Object principal = authentication.getPrincipal();

    String rediskey = String.valueOf(principal);
    if (principal != null) {
      try {
        String jsonStr = String.valueOf(principal);
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        rediskey = jsonObject.getString("userId");
      } catch (Exception e) {
      }
    }
    return rediskey+":"+redisSuffx;
  }
  @Override
  public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
//    log.info("=======>JwtRedisTokenStore readAuthentication token value:{}",token.getValue());
    OAuth2Authentication auth2Authentication = readAuthentication(token.getValue());

    String tokenValue = stringRedisTemplate.opsForValue().get(getRedisKey(auth2Authentication));
    /**
     * 每次创建token都会覆盖redis里的缓存，@link readAuthentication 的时候如果token不一致则验证不通过
     */
//    log.debug("=======>JwtRedisTokenStore readAuthentication token value:{}",token.getValue());
//    log.debug("=======>JwtRedisTokenStore readAuthentication token value:{}",tokenValue);
    if(StringUtils.equalsIgnoreCase(token.getValue(),tokenValue)) {
      return auth2Authentication;
    }
    return null;
  }

  @Override
  public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
    /**
     * 每次创建token都会覆盖redis里的缓存，@link readAuthentication 的时候如果token不一致则验证不通过
     */
    super.storeAccessToken(token,authentication);
    stringRedisTemplate.opsForValue().set(getRedisKey(authentication), token.getValue(),token.getExpiresIn(), SECONDS);
  }


}
