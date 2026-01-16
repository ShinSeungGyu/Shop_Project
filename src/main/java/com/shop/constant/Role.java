package com.shop.constant;

public enum Role {
    USER("ROLE_USER"), // 생성자로 "ROLE_USER" 문자열 전달
    ADMIN("ROLE_ADMIN"); // 생성자로 "ROLE_ADMIN" 문자열 전달

    private final String key; // 각 Role 상수에 매핑될 문자열 키

    // 생성자 추가
    Role(String key) {
        this.key = key;
    }

    // key 값을 반환하는 getter 메서드
    public String getKey() {
        return key;
    }
}