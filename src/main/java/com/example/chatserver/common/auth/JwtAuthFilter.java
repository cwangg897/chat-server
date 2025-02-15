package com.example.chatserver.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.security.sasl.AuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

// 싱글톤 객체로 만드는게 중요
// Authentication객체 만들어줘야함
// Authentication 객체는 Spring Security에서 사용되는 인증 정보를 나타내는 객체
// 토큰검증
@Component
public class JwtAuthFilter extends GenericFilter {

    @Value("${jwt.secretKey}")
    private String secretKey;

    // return 안해주면 필터에서 머무르게 됨

    /**
     * @param request  사용자에게 토큰받아서 꺼내야함
     * @param response 토큰이 비정상이면 response에 에러반환 아니면 chain으로 다음필터로 이동
     * @param chain    chain은 다시 필터로 되돌리는 역할임
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String token = httpServletRequest.getHeader("Authorization");
        try {
            if(token != null){
                if(!token.substring(0, 7).equals("Bearer ")){
                    throw new AuthenticationException("Bearer 형식이 아닙니다");
                }
                String jwtToken = token.substring(7);

                // 토큰을 다시 까서 시그니처 부분이 암호화되어있어서 여기만 검증하기
                Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey) // 서명부분을 똑같이 넣어서 검증하는거임 token을 보면 시그니처 = header + payload + secretKey 이렇기때문에 같은것을 넣어서 검증 token에 이미 서명이있으니까
                    .build()
                    .parseClaimsJws(jwtToken) // 여기까지는 우리가 토큰을 다시한번 만듬  (signedKey는 = header + payload + secretKey 이기떄문에 라이브러리안에 파싱하면서 다시암호화시키는게 들어가있음 (parseClaimsJws, parseClaimsJwt 다르다)
                    .getBody(); // Claims객체가 나온다  Header는 잘안보는 이유 GPT에 나옴 알고리즘, 타입명시는 잘 안바뀌기때문에



                // Authentication객체 생성 스프링 시큐리티에서 사용해야하는거
                List<GrantedAuthority> authorityList = new ArrayList<>();
                authorityList.add(new SimpleGrantedAuthority("ROLE_"+claims.get("role"))); // 관례적으로 ROLE_가 들어간다

                UserDetails userDetails = new User(claims.getSubject(), "", authorityList);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities()); // 비밀번호 안찍음
                SecurityContextHolder.getContext().setAuthentication(authentication); // 계층구조 이런거 알면좋음 메모하기 (시큐리티 컨텍스트에 넣음 )
            }
        }catch (Exception e){
            e.printStackTrace();
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.setContentType("application/json");
            httpServletResponse.getWriter().write("invalid token");
        }
        // 토큰비어있는경우는 로그인이나 이런경우
        chain.doFilter(request, response);
    }
}
