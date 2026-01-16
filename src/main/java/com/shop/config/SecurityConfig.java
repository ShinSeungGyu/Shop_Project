package com.shop.config;

import com.shop.service.CustomOAuth2UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(ClientRegistrationRepository clientRegistrationRepository, CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authorizeHttpRequestsCustomizer -> authorizeHttpRequestsCustomizer
                        .requestMatchers("/css/**", "/js/**", "/img/**", "icons/**").permitAll()
                        .requestMatchers("/", "/members/**", "/item/**", "/images/**", "/jusoPopup").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest()
                        .authenticated()
                )
                .formLogin(formLoginCustomizer -> formLoginCustomizer //Form 기반 인증
                        .loginPage("/members/login") //이메일과 비밀번호를 입력받을 로그인 페이지의 url
                        .defaultSuccessUrl("/", true) //로그인 성공 시 이동할 url
                        .usernameParameter("email") //사용자명(username)을 담을 파라미터의 이름을 지정
                        .failureHandler(new CustomAuthenticationFailureHandler()) //실패 시 처리할 코드 지정
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/members/login")
                        .authorizationEndpoint(authorization -> authorization   //소셜 로그인으로 떠나는 시점(엔드포인트)을 설정한다.
                                .authorizationRequestResolver(customAuthorizationRequestResolver()) //그 때 사용할 요청(RequestResolver)는 custom... 을 사용하겠다.
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .defaultSuccessUrl("/", true)
                        .failureHandler(new CustomAuthenticationFailureHandler())
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                        .logoutSuccessHandler(new CustomLogoutSuccessHandler()) // 커스텀 핸들러 등록
                        .deleteCookies("JSESSIONID", "remember-me")
                        .invalidateHttpSession(true)
                ).csrf(csrf->csrf
                    .ignoringRequestMatchers("/jusoPopup"))
                    .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver() {
        // 조금 더 정교한 분기를 위해 커스텀 로직을 적용한 Resolver 반환 >> CustomRequestResolver 클래스 호출
        return new CustomRequestResolver(clientRegistrationRepository);
    }
}