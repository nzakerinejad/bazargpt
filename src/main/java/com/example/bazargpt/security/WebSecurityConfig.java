package com.example.bazargpt.security;

import com.example.bazargpt.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> {auth
                        .requestMatchers("/users").hasRole("ADMIN")
                        .requestMatchers("/admin_chat/{email}", "/admin_conversation/{conversationId}").hasRole("ADMIN")
                        .requestMatchers("/conversation", "/chat", "/conversation/{conversationId}").authenticated()
                        .anyRequest().permitAll();
                })
                .formLogin(login -> login
                        .usernameParameter("email")
                        .defaultSuccessUrl("/index")
                        .permitAll())
                .rememberMe(rememberMe -> rememberMe.key("uniqueAndSecret"))
                .logout(logout -> logout.logoutSuccessUrl("/").permitAll())
                .build();
    }
}
