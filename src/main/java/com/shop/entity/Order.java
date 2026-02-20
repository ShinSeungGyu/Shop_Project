package com.shop.entity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.shop.constant.OrderStatus;

import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name="order_id")
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @Column(name="orderName")
    private String orderName;

    @Column(name = "toss_order_id", unique = true, nullable = false, length = 64)
    private String tossOrderId; // Toss에서 사용하는 문자열 orderId

    @Column(name = "toss_paymentKey")
    private String tossPaymentKey;

    private LocalDateTime orderDate; //주문날짜

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; //주문상태

    @OneToMany(mappedBy="order", cascade= CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>(); //주문목록

    @Column(name = "trackingNumber", length = 100)
    private String trackingNumber; //운송장번호

    @Column(name="courier")
    private String courier; //배송사 코드

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList, String orderName) {
        Order order = new Order();
        order.setMember(member);
        order.setTossOrderId("order-" + UUID.randomUUID());
        for(OrderItem orderItem: orderItemList) {
            order.addOrderItem(orderItem);
        }

        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderName(orderName);
        return order;
    }

    public int getTotalPrice() {
        int totalPrice = 0;
        for(OrderItem orderItem: orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    public void cancelOrder() { //주문에 들어있던 상품들을 모두 취소 상태로 변환
        this.orderStatus = OrderStatus.CANCEL;

        for(OrderItem orderItem:orderItems){
            orderItem.cancel();
        }
    }
}