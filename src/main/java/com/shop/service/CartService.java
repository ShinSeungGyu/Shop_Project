package com.shop.service;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartOrderDto;
import com.shop.dto.OrderDto;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.dto.CartItemDto;
import com.shop.entity.Member;
import com.shop.repository.CartItemRepository;
import com.shop.repository.CartRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    public Long addCart(CartItemDto cartItemDto, String email){
        Item item = itemRepository.findById(cartItemDto.getItemId()).orElseThrow(EntityNotFoundException::new); //장바구니에 담을 상품
        Member member = memberRepository.findByEmail(email); //현재 로그인 회원 조회

        Cart cart = cartRepository.findByMemberId(member.getId()); //회원의 장바구니를 조회
        if(cart == null){ //조회된 장바구니가 없다면
            cart = Cart.createCart(member); //장바구니 생성
            cartRepository.save(cart);
        }

        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId()); //상품이 장바구니에 들어있는지 조회

        if(savedCartItem != null){  //상품이 장바구니에 들어있다면
            savedCartItem.addCount(cartItemDto.getCount()); //기존 수량에 카운트만큼 추가
            return savedCartItem.getId();
        }
        else { //상품이 장바구니에 없다면(null)
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }

    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email){
        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId());
        if(cart == null){
            return cartDetailDtoList;
        }
        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
        return cartDetailDtoList;
    }

    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email){
        Member curMember = memberRepository.findByEmail(email);
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
        Member savedMember = cartItem.getCart().getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())) {
            return false;
        }
        return true;
    }

    public void updateCartItemCount(Long cartItemId, int count){
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
        cartItem.updateCount(count);
    }

    public void deleteCartItem(Long cartItemId){
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }

    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email){
        List<OrderDto> orderDtoList = new ArrayList<>();
        for(CartOrderDto cartOrderDto : cartOrderDtoList){
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId()).orElseThrow(EntityNotFoundException::new);

            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }

        Long orderId = orderService.orders(orderDtoList, email); //장바구니에 담은 상품을 주문

        for(CartOrderDto cartOrderDto: cartOrderDtoList){ //주문한 상품들을 장바구니에서 제거
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId()).orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem);
        }

        return orderId;
    }
}
