package com.example.chatserver.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // column안붙여도 생성되네..

    @Column(nullable = false, unique = true)
    private String email;

    private String password; // varchar255로 기본으로 들어감

    @Enumerated(value = EnumType.STRING)
    @Builder.Default // Builder.Default로 해야하지 기본적으로 Role.USER로 들어가는데 Builder로 만들 때 Default로 만들어준다 라는거
    private Role role = Role.USER; // 입력안하는경우
}
