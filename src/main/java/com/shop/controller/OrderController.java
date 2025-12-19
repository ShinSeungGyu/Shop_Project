package com.shop.controller;
import com.shop.dto.OrderHistDto;
import com.shop.entity.*;
import com.shop.repository.CartItemRepository;
import com.shop.repository.CartRepository;
import com.shop.repository.OrderRepository;
import com.shop.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Value("${toss.payments.secretKey}")
    private String tossPaymentsSecretKey;

    @Value("${toss.payments.api.url}")
    private String tossPaymentsApiUrl;

    @GetMapping("/order/success") // "/order/success" 경로의 GET 요청을 처리
    public String paymentsSuccess(@RequestParam String paymentKey, @RequestParam String orderId, @RequestParam Long amount, Model model) {
        String requestBodyJson = String.format("{\"paymentKey\":\"%s\",\"orderId\":\"%s\",\"amount\":%d}", paymentKey, orderId, amount);
        HttpRequest request = HttpRequest.newBuilder() //Toss에 결제 승인을 요청하는 코드
                .uri(URI.create(tossPaymentsApiUrl))
                .header("Authorization", "Basic " + encodeSecretKeyForBasicAuth(tossPaymentsSecretKey))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            model.addAttribute("response", response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        //결제 승인까지 끝났으니 장바구니에서 주문된 아이템들을 제거하는 코드
        Order order = orderRepository.findTossOrder(orderId);
        Member member = order.getMember();
        List<OrderItem> orderItems = order.getOrderItems();
        Cart cart = cartRepository.findByMemberId(member.getId());
        for (OrderItem orderItem : orderItems) { //주문한 상품들을 장바구니에서 제거
            Item orderedItem = orderItem.getItem();;
            int orderItemCount = orderItem.getCount();
            CartItem cartItem = cartItemRepository.findByCartAndItem(cart, orderedItem).orElseThrow(EntityNotFoundException::new);
            if (cartItem.getCount() > orderItemCount) {
                cartItem.minusCount(orderItemCount);
            }
            else {
                cartItemRepository.delete(cartItem);
            }
        }
        
        // 클라이언트에서 전달받은 파라미터들을 Model에 추가하여 Thymeleaf 템플릿으로 전달합니다.
        model.addAttribute("paymentKey", paymentKey);
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);
        return "order/success";
    }

    public static String encodeSecretKeyForBasicAuth(String tossPaymentsSecretKey) {
        // 1. Secret Key 뒤에 콜론(:)을 붙입니다. (HTTP Basic Auth 표준)
        String credentials = tossPaymentsSecretKey + ":";
        // 2. 이 문자열을 UTF-8 바이트 배열로 변환합니다.
        byte[] credentialsBytes = credentials.getBytes(StandardCharsets.UTF_8);
        // 3. 바이트 배열을 Base64로 인코딩합니다.
        String encodedAuthKey = Base64.getEncoder().encodeToString(credentialsBytes);

        return encodedAuthKey;
    }

    @GetMapping("/order/fail")
    public String paymentsFail(@RequestParam String code, @RequestParam String message, @RequestParam String orderId, Model model) {
        model.addAttribute("errorMessage", message);
        model.addAttribute("errorCode", code);
        model.addAttribute("orderId", orderId);
        Order order = orderRepository.findTossOrder(orderId);
        orderService.cancelOrder(order.getId());
        return "order/fail"; // src/main/resources/templates/order/fail.html
    }
    //------------------------------------------------------------------------------------------------------------
    //구매이력
    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page")Optional<Integer> page, Principal principal, Model model) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);
        Page<OrderHistDto> ordersHistDtoList = orderService.getOrderList(principal.getName(), pageable);
        model.addAttribute("orders", ordersHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);
        return "order/orderHist";
    }
    //------------------------------------------------------------------------------------------------------------
    //현재 사용자와 주문번호를 받아 주문자와 사용자가 일치하는지 검증 후 삭제를 진행
    @PostMapping(value = "/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId, Principal principal) {
        if(!orderService.validateOrder(orderId, principal.getName())) {
            return new ResponseEntity<>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        orderService.cancelOrder(orderId);
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }
}
