package com.shop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberProfileDto {
    @NotBlank(message = "이름은 필수 항목입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @Size(max = 255, message = "주소는 최대 255자까지 입력할 수 있습니다.")
    private String address;

    // TODO: 비밀번호 변경 필드 추가 시 여기에 currentPassword, newPassword, confirmNewPassword 추가
}
