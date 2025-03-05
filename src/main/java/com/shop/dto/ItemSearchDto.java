package com.shop.dto;

import com.shop.constant.ItemSellStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchDto {

    //조회 기간
    private String searchDateType;

    //판매 상태
    private ItemSellStatus searchSellStatus;

    //상품 조회 유형 : 상품명 or 상품 등록자 ID
    private String searchBy;

    //조회할 검색어 저장 변수
    private String searchQuery = "";

}
