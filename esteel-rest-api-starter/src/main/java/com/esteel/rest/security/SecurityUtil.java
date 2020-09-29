package com.esteel.rest.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

public class SecurityUtil {

    public static User getUser(OAuth2Authentication authentication) {
        if (authentication != null && authentication.getDetails() != null) {
            User user = new User(authentication.getPrincipal().toString(), authentication.getAuthorities());
            user.setId(user.getUsername());

            Map<String, Object> decodedDetails = getDetails(authentication);
            if (decodedDetails != null) {
                String displayName = (String) decodedDetails.get("display-name");
                if (StringUtils.isNotBlank(displayName)) {
                    try {
                        String decoded = URLDecoder.decode(displayName, "UTF-8");
                        user.setDisplayName(decoded);
                    } catch (UnsupportedEncodingException ignore) {
                    }
                }
            }
            return user;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getDetails(OAuth2Authentication authentication) {
        if (OAuth2AuthenticationDetails.class.isInstance(authentication.getDetails())) {
            OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
            return (Map<String, Object>) details.getDecodedDetails();
        } else if (Map.class.isInstance(authentication.getDetails())) {
            return (Map<String, Object>) authentication.getDetails();
        }
        return null;
    }
}
