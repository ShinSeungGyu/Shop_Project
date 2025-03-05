package com.shop.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.entity.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>{

    @Query("select o from Order o " + "where o.member.email = :email " + "order by o.orderDate desc")
    List<Order> findOrders(@Param("email") String email, Pageable pageable); //현재 로그인 회원의 주문 데이터를 페이징

    @Query("select count(o) from Order o where o.member.email = :email")
    Long countOrder(@Param("email") String email); //현재 로그인한 회원의 주문 갯수 조회

}
