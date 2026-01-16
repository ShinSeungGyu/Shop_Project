package com.shop.service;

import com.shop.entity.Member;
import com.shop.repository.MemberRepository;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository; // 사용자 정보를 저장하는 Repository

    public CustomOAuth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 기본 OAuth2UserService의 loadUser() 호출
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 2. 구글에서 가져온 사용자 정보 추출
        String email = null; // 이메일
        String name = null;   // 이름
        // 기타 필요한 정보 (picture, locale 등)도 oAuth2User.getAttributes()에서 추출 가능
        if ("google".equals(registrationId)) {
            email = oAuth2User.getAttribute("email");
            name = oAuth2User.getAttribute("name");
        }else if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
            if (kakaoAccount != null) {
                email = (String) kakaoAccount.get("email");
                name = (String) kakaoAccount.get("name");
            }
        } else if ("naver".equals(registrationId)) {
            Map<String, Object> response = oAuth2User.getAttribute("response"); // 네이버는 "response" 키 아래에 사용자 정보가 있습니다.
            if (response != null) {
                email = (String) response.get("email");
                name = (String) response.get("name");
            }
        }
        System.out.println(email);
        System.out.println(name);
        System.out.println(oAuth2User.getAttributes());
        // 3. 추출한 정보로 회원 관리 (DB 연동)
        // 예를 들어, User 엔티티를 찾아보거나 새로 생성합니다.
        String updateName = name;
        Member member = memberRepository.findByEmail(email)
                .map(entity -> entity.update(updateName)) // 기존 사용자 정보 업데이트 로직
                .orElse(Member.createSocialMember(email, name, null)); // 신규 사용자 등록 로직 (정적 팩토리 메서드 활용)

        memberRepository.save(member); // DB에 저장 또는 업데이트

        if ("kakao".equals(registrationId)) {
            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority(member.getRole().getKey())),
                    oAuth2User.getAttribute("kakao_account"),
                    "email"); // oAuth2User.getAttributes()에서 사용자 고유 식별자로 사용할 필드명
        } else if ("naver".equals(registrationId)) {
            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority(member.getRole().getKey())),
                    oAuth2User.getAttribute("response"),
                    "email");
        }
        // 4. Spring Security가 세션에 저장할 Principal 객체 반환
        // 이때 사용자의 ID (Primary Key)와 권한을 함께 넘겨줍니다.
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRole().getKey())),
                oAuth2User.getAttributes(),
                "email"); // oAuth2User.getAttributes()에서 사용자 고유 식별자로 사용할 필드명
    }
}