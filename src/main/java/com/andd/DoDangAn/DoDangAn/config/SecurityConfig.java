package com.andd.DoDangAn.DoDangAn.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    @Autowired
    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/user/register", "/user/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/login", "/user/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/", "/user/home", "/categories/**", "/movie/preshow/**",
                                "/assets/**", "/uploads/**", "/upload_dir/**", "/views/**", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/movie/movies", "/movie/movies/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/movie/show/**", "/user/movielist/**").authenticated()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendRedirect("/user/login?error=PleaseLogin");
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL để gọi logout
                        .addLogoutHandler((request, response, authentication) -> {
                            // Xóa cookie jwt
                            jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("jwt", null);
                            cookie.setPath("/");
                            cookie.setHttpOnly(true);
                            cookie.setMaxAge(0); // Xóa cookie ngay lập tức
                            response.addCookie(cookie);
                        })
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // Chuyển hướng sau khi đăng xuất thành công
                            response.sendRedirect("/user/login?logout=success");
                        })
                        .invalidateHttpSession(true) // Không cần thiết với STATELESS, nhưng để đảm bảo tương thích
                        .clearAuthentication(true) // Xóa SecurityContext
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}