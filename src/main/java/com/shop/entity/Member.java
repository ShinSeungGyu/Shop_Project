package com.shop.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;

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
}
