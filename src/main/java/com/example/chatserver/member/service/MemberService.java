package com.example.chatserver.member.service;

import com.example.chatserver.common.auth.JwtTokenProvider;
import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.domain.Role;
import com.example.chatserver.member.dto.MemberListResDto;
import com.example.chatserver.member.dto.MemberLoginRequestDto;
import com.example.chatserver.member.dto.MemberSaveReqDto;
import com.example.chatserver.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public Member create(MemberSaveReqDto saveReqDto) {
        if(memberRepository.findByEmail(saveReqDto.getEmail()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 이메일입니다");
        }
        Member member = Member.builder()
            .email(saveReqDto.getEmail())
            .name(saveReqDto.getName())
            .password(passwordEncoder.encode(saveReqDto.getPassword()))
            .build();
        return memberRepository.save(member);
    }

    public Member doLogin(MemberLoginRequestDto requestDto) {
        Member member = memberRepository.findByEmail(requestDto.getEmail())
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 이메일입니다"));
        if(!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())){
            throw new IllegalArgumentException("비밀번호가 틀려요");
        }
        return member;
    }

    public String issueToken(String email, Role role){
        return jwtTokenProvider.createToken(email, role.toString());
    }

    public List<MemberListResDto> finall() {
        return memberRepository.findAll().stream()
            .map(m -> new MemberListResDto(m.getId(), m.getName(), m.getEmail()))
            .toList();
    }
}
