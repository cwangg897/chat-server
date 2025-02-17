package com.example.chatserver.common.configs;

import com.example.chatserver.common.auth.JwtAuthFilter;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// spring 3.x security
@Configuration
@RequiredArgsConstructor
public class SecurityConfigs {

    private final JwtAuthFilter jwtAuthFilter;
    // 내가 커스텀해서 만든 SpringFilter를 리턴해줌 그리고 빈을 등록하게되고 Filter로 등록되어 사용됨
    @Bean
    public SecurityFilterChain myFilter(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            .cors(cors-> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable) // csrf공격에 대비하지않겠다 (서비스를 운영하면서 방어할 수 있는게 많아서 필터에서 주로 비활성화함)
            /**
             * HTTP 요청 헤더에 ID와 비밀번호를 실어서 인증하는 방식이야.
             * 비활성화 하는경우
             * JWT 같은 토큰 기반 인증 쓸 때
             * 세션 기반 로그인(폼 로그인) 쓸 때
             * API 서버만 운영해서 굳이 ID/PW 인증 팝업 같은 거 필요 없을 때
             */
            .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 비활성화 (보안인증방법중하나인데 설정안하면 spring기본적으로 Http Basic활성화 시킴
            // 특정 URL패턴에 대해서는 Authentication객체 요구하지 않음.  Authentication 객체는 Spring Security에서 사용되는 인증 정보를 나타내는 객체
            // connect에 대해서는 Authentication만들어야하는 의무 없다
            .authorizeHttpRequests(a -> a.requestMatchers("/members/create", "/members/do-login", "/connect").permitAll().anyRequest().authenticated())
            .sessionManagement(s-> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션방식을 사용하지 않겠다라는 의미 토큰방식을 쓰기때문에
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // 검증코드는 JwtAuthFilter이고 해당클래스를 통해검증하겠다라고 적기 싱글톤으로 만들어서 Bean으로 주입받기
            .build();
    }



    // 예외적으로 도메인이 달라도 허용해주는거 설정 cors에 대해서 깊이 공부해보기
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("*")); // 모든 HTTP메소드 허용
        configuration.setAllowedHeaders(List.of("*")); // 모든 헤더값 허용
        configuration.setAllowCredentials(true); // 자격증명을 허용한다 (인증작업고나련한 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 url패턴에 대해 cors허용 설정 http://localhost:3000 도메인의 url패턴을 다허용
        return source;
    }


    // 암호화 라이브러리
    // 패스워드관련한 빈객체가 생성
    @Bean
    public PasswordEncoder makePassword(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
