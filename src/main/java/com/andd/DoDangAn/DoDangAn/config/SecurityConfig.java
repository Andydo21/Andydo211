package com.andd.DoDangAn.DoDangAn.config;

import com.andd.DoDangAn.DoDangAn.models.Role;
import com.andd.DoDangAn.DoDangAn.models.User;
import com.andd.DoDangAn.DoDangAn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/products/**").permitAll()
                        .requestMatchers("/movie/**").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                );

        return http.build();
    }
    @Configuration
    @Order(1)
    public static class AdminConfigurationAdapter {
        @Bean
        public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(new AntPathRequestMatcher("/admin/login")).permitAll()
                            .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
                    )
                    .formLogin(form -> form
                            .loginPage("/admin/login")
                            .loginProcessingUrl("/admin/LoginValidate")
                            .successHandler((request, response, authentication) ->
                                    response.sendRedirect("/admin/")
                            )
                            .failureHandler((request, response, exception) ->
                                    response.sendRedirect("/admin/login?error=true")
                            )
                    )
                    .logout(logout -> logout
                            .logoutUrl("/admin/logout")
                            .logoutSuccessUrl("/admin/login")
                            .deleteCookies("JSESSIONID")
                    )
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    );

            return http.build();
        }
    }
    @Configuration
    @Order(2)
    public static class UserConfigurationAdapter {

        @Bean
        public SecurityFilterChain userFilterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/login", "/register").permitAll()
                            .requestMatchers("/**").hasRole("USER")
                    )
                    .formLogin(form -> form
                            .loginPage("/login")
                            .loginProcessingUrl("/userloginvalidate")
                            .successHandler((request, response, authentication) -> response.sendRedirect("/")) // Redirect on success
                            .failureHandler((request, response, exception) -> response.sendRedirect("/login?error=true")) // Redirect on failure
                    )
                    .logout(logout -> logout
                            .logoutUrl("/logout")
                            .logoutSuccessUrl("/login")
                            .deleteCookies("JSESSIONID")
                    )
                    .exceptionHandling(exception -> exception
                            .accessDeniedPage("/403") // Custom 403 page
                    )
                    .csrf(csrf -> csrf.disable());

            return http.build();
        }
    }
    @Bean
    UserDetailsService userDetailsService() {


        return username -> {
            String userrole="USER";
            User user = userRepository.findByUsername(username);

            if(user == null) {
                throw new UsernameNotFoundException("User with username " + username + " not found.");
            }
            Set<Role> roles = user.getRoles();
            for(Role role : roles) {
                if(role.getName().equals("ROLE_ADMIN")) {
                    userrole="ADMIN";
                }
            }


            return org.springframework.security.core.userdetails.User
                    .withUsername(username)
                    .passwordEncoder(input->passwordEncoder().encode(input))
                    .password(user.getPassWord())
                    .roles(userrole)
                    .build();
        };
    }


    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web)->web.ignoring().requestMatchers("/views/**");
    }
}
