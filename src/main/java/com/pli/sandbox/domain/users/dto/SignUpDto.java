package com.pli.sandbox.domain.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SignUpDto {

    @Getter
    @Schema(description = "사용자 회원가입 요청")
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @Schema(description = "사용자 이메일 주소", example = "chordplaylist@gmail.com")
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        private String email;

        @Schema(description = "사용자 비밀번호 (8자 이상)", example = "password1234")
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        private String password;

        @Schema(description = "사용자 닉네임 (최대 32자)", example = "pli")
        @NotBlank(message = "Nickname cannot be blank")
        @Size(max = 32, message = "Nickname cannot exceed 32 characters")
        private String nickname;

        public static Request of(String email, String password, String nickname) {
            Request request = new Request();
            request.email = email;
            request.password = password;
            request.nickname = nickname;
            return request;
        }
    }
}
