package com.shop.config;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class CustomRequestResolver implements OAuth2AuthorizationRequestResolver {
    private final OAuth2AuthorizationRequestResolver defaultResolver;

    public CustomRequestResolver(ClientRegistrationRepository repo) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");
    }
    //
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        return customize(defaultResolver.resolve(request));
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        return customize(defaultResolver.resolve(request, clientRegistrationId));
    }

    private OAuth2AuthorizationRequest customize(OAuth2AuthorizationRequest request) {
        if (request == null) return null;

        if ("google".equals(request.getAttribute(OAuth2ParameterNames.REGISTRATION_ID))) {
            Map<String, Object> extraParams = new HashMap<>(request.getAdditionalParameters());
            //필수 정보 외의 파라미터들을 Map 형태로 가져온다.
            //읽기 전용인 Map에 바로 put 하면 에러가 발생하므로, new HashMap을 통해 기존 내용을 복제하면서 수정할 수 있게끔 한다.
            extraParams.put("prompt", "select_account"); //거기에 prompt=select_account 를 추가한다.

            return OAuth2AuthorizationRequest.from(request).additionalParameters(extraParams).build();
        }
        return request;
    }
}