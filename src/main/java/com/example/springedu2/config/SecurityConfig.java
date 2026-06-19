package com.example.springedu2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// intellij
// File-> Settings
//    -> Editor → Inlay Hints → Java
//       Parameter names :java 체크해제,
//       types           : java 체크 해제

// 주석 잘보이게
//Editor
// └─ Color Scheme
//     └─ Language Defaults
//         └─ Comments
// Line comment      // 한 줄 주석
// Block comment     /* 여러 줄 주석 */
// Doc comment       /** 문서 주석 */
//  Bold 체크


// SpringSecurity 의 설정
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 서버가 내려주는 csrf 토큰값을 사용하지 않는다
                // visiorForm.html 순수 html 이라 서버의 csrf 토큰을 보관 처리 불가능한다
                .csrf(csrf->csrf.disable()) // 실수는 설정, 공부 설정안함
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/index.html",
                                "/css/**", "/img/**", "/js/**", "/fonts/**",
                                "/login", "/members/register"
                        ).permitAll()   // 로그인없이 사용가능하다
                        .requestMatchers("/admin/**", "/vupdate", "/vdelete").hasRole("ADMIN")
                        .requestMatchers(
                                "/visitorMain.html", "/visitorForm.html",
                                "/vlist", "/vinsert",  "/vsearch",  "/one",
                                "/members/me"
                        ).authenticated() // 로그인이 필요해
                        .anyRequest().authenticated() // 설정하지 않은 다른 요청도 로그인필요
                )
                // formLogin() 는 사용자가 <form> 으로 입력한 username, password 를 기반으로 인증 처리
                .formLogin( form -> form
                        .loginPage("/login")
                          // GET /login ->  PageController 에 /login 주소이동 -> login.html 로 보낸다
                          // 내가 만든 로그인화면 사용
                        .loginProcessingUrl("/login")  // 생략가능
                         //  Post /login
                         //  Spring Security 가 username, password 읽어서 인증처리한다 : 자동
                        .defaultSuccessUrl("/visitorMain.html", true)
                        .permitAll()
                )
                .logout(logout -> logout.logoutUrl("/logout"))
                .exceptionHandling(
                        exception ->
                                exception.accessDeniedPage("/access-denied")
                );  // 접근 거부 페이지 처리
        return http.build();

    }

    // 비밀번호를 암호화
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
