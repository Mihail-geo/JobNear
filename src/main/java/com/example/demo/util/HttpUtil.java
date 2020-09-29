package com.example.demo.util;

import lombok.experimental.UtilityClass;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import com.example.demo.constant.SecurityConstant;

import java.util.Map;


@UtilityClass
public class HttpUtil {
    public static Long getManagerIdFromAuthentication(Authentication authentication) {
        if (authentication == null) return null;
        Map<String, Object> values = getMapClaimsFromTokenInAuthentication(authentication);
        return Long.valueOf(values.get(SecurityConstant.MANAGER_ID_FIELD_IN_TOKEN).toString());
    }

    public static Map<String, Object> getMapClaimsFromTokenInAuthentication(Authentication authentication) {
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
        String claims = JwtHelper.decode(details.getTokenValue()).getClaims();
        return JsonParserFactory.getJsonParser().parseMap(claims);
    }
}
