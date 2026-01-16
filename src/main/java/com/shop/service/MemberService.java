package com.shop.service;
import com.shop.dto.MemberProfileDto;
import com.shop.entity.Member;
import com.shop.repository.MemberRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public void saveMember(Member member){
        validateDuplicateMember(member);
        memberRepository.save(member);
    }

    public void updateMember(String email, MemberProfileDto memberProfileDto){
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));

        // 소셜 로그인 정보는 주로 name, email이므로, 여기서는 address만 수정 가능하게 예시를 듭니다.
        // 필요에 따라 name, email도 수정 가능하게 할 수 있습니다.
        // user.setName(form.getName()); // 이름 수정 가능하도록 하려면 이 줄을 활성화
        // user.setEmail(form.getEmail()); // 이메일 수정 가능하도록 하려면 이 줄을 활성화
        member.setAddress(memberProfileDto.getAddress());

        memberRepository.save(member); // 변경된 사용자 정보 저장

        // TODO: 비밀번호 변경 로직은 여기 추가 (현재 비밀번호 확인, 새 비밀번호 암호화 등)
    }

    private void validateDuplicateMember(Member member){
        Member findMember = memberRepository.findByEmail(member.getEmail()).orElse(null);
        if(findMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email).orElse(null);
        if (member == null) {
            throw new UsernameNotFoundException(email);
        }

        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }

}
