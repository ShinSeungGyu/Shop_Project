package com.shop.config;

import jakarta.servlet.ServletException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        if (authentication instanceof OAuth2AuthenticationToken oAuth2Token) {
            String registrationId = oAuth2Token.getAuthorizedClientRegistrationId();

            // 1. 카카오 로그인 사용자인 경우
            if ("kakao".equals(registrationId)) {
                String clientId = "b1aac96b1318c34864a359108ead81c2"; // 카카오 REST API 키
                String logoutRedirectUri = "http://localhost/";
                String kakaoLogoutUrl = "https://kauth.kakao.com/oauth/logout?client_id=" + clientId + "&logout_redirect_uri=" + logoutRedirectUri;

                response.sendRedirect(kakaoLogoutUrl);
                return;
            }
        }

        // 일반 로그인이나 기타 SNS 로그인은 기본 페이지로 이동
        try {
            super.onLogoutSuccess(request, response, authentication);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }
}
