package com.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByMemberId(Long memberId); //현재 로그인한 회원의 Cart 엔티티 찾기
}
