package com.nailshop.nailborhood.dto.member.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequestDto {
    private String email;
    private String password;
    private String name;
    private LocalDate birthday;
    private String phoneNum;
    private String gender;
    private String address;
    private String nickname;
}
