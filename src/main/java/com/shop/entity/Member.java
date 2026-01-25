package com.shop.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="member")
@Getter
@Setter
@ToString
public class Member extends BaseEntity{
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Cart cart;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Order> orders = new ArrayList<>();

    public static Member createMember(MemberFormDto memberForm, PasswordEncoder passwordEncoder) {
        Member member = new Member();
        member.setName(memberForm.getName());
        member.setEmail(memberForm.getEmail());
        member.setAddress(memberForm.getAddress());
        String password = passwordEncoder.encode(memberForm.getPassword());
        member.setPassword(password);
        member.setRole(Role.ADMIN);
        return member;
    }

    public static Member createSocialMember(String email, String name, String address){
        Member member = new Member();
        member.setEmail(email);
        member.setName(name);
        member.setAddress(address); // 구글에서 가져온 주소 정보가 있다면 활용
        member.setPassword(null);   // 소셜 로그인 사용자는 비밀번호가 없음
        member.setRole(Role.USER);       // 역할 설정 (예: Role.USER)
        return member;
    }
    public Member update(String name) {
        // 구글에서 새로 받아온 이름으로 Member 객체의 이름을 업데이트합니다.
        this.name = name;
        // 필요하다면 프로필 사진 등 다른 정보도 여기서 업데이트할 수 있습니다.
        // 예: this.picture = picture;
        return this; // 중요한 부분: 현재 객체(this)를 반환해야 스트림 API의 map()이 계속 동작합니다.
    }
}
