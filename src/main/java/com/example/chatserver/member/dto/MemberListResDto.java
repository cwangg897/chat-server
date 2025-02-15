package com.example.chatserver.member.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberListResDto {

    private Long id;
    private String name;
    private String email;
}
