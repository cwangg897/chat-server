package com.example.chatserver.member.controller;

import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.dto.MemberListResDto;
import com.example.chatserver.member.dto.MemberLoginRequestDto;
import com.example.chatserver.member.dto.MemberSaveReqDto;
import com.example.chatserver.member.service.MemberService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody MemberSaveReqDto saveReqDto){
        Member member = memberService.create(saveReqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(member.getId());
    }

    @PostMapping("/do-login")
    public ResponseEntity<?> doLogin(@RequestBody MemberLoginRequestDto requestDto){
        // email, password검증
        Member member = memberService.doLogin(requestDto);
        // 일치할 경우 access발행
        String jwtToken = memberService.issueToken(member.getEmail(), member.getRole());
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", member.getId());
        loginInfo.put("token", jwtToken);
        return ResponseEntity.ok(loginInfo);
    }

    @GetMapping()
    public ResponseEntity<?> memberList(){
        List<MemberListResDto> dtos = memberService.finall();
        return ResponseEntity.ok(dtos);
    }

}
