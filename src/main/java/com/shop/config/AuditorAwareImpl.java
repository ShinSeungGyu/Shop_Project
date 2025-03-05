package com.shop.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditorAwareImpl implements AuditorAware<String> {
    //AuditorAware 인터페이스를 통해 현재 사용자의 id를 반환한다.
    @Override
    public Optional<String> getCurrentAuditor() {
        //사용자의 인증정보를 가져온다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = "";

        if(authentication != null) { //사용자가 인증된 경우(로그인 된 경우)
            userId = authentication.getName(); //현재 사용자의 이름(ID)를 가져와 저장한다.
        }
        return Optional.of(userId);
    }

}
