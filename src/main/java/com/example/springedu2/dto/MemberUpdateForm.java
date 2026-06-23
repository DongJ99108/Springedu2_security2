package com.example.springedu2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberUpdateForm {

    // @NotBlank : NULL, ""(빈 문자열), "  "(공백 문자열)
    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 50, message = "이름은 50자 이내로 입력하세요")
    private String name;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식으로 입력하세요")
    @Pattern(
            regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", // 이메일의 유효성을 검사하는 정규표현식
            message = "이메일 도메인에는 .이 포함되어야 합니다."
    )  // jakarta
    @Size(max = 120, message = "이메일은 120자 이내로 입력하세요")
    private String email;

    @Size(max = 100, message = "비밀번호는 100자 이내로 입력하세요")
    private String password;

    private String role = "USER";

    private boolean enabled = true;

}
