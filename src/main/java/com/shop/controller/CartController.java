package com.shop.controller;

import com.shop.dto.CartItemDto;
import com.shop.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/cart")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto, BindingResult bindingResult, Principal principal){
        if(bindingResult.hasErrors()){ //데이터 바인딩시 검증에 위반된 것이 하나라도 있다면,
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors(); //바인딩에 담긴 필드에러를 리스트에 모두 담고
            for(FieldError fieldError:fieldErrors){
                sb.append(fieldError.getDefaultMessage()); //모든 에러 메시지를 sb에 담는다.
            }
            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = principal.getName(); //현재 사용자의 email 을 받아온다.
        Long cartItemId;

        try{
            cartItemId = cartService.addCart(cartItemDto, email); //화면으로부터 받은 상품정보와 회원정보를 이용해 장바구니에 상품 담는 로직 호출
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    }


}
