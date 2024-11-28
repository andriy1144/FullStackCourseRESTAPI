package org.studyeasy.SpringRestDemo.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public InMemoryUserDetailsManager users(){
        return new InMemoryUserDetailsManager(
            User.withUsername("andr")
            .password("{noop}password")
            .authorities("read")
            .build()
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(request -> request.requestMatchers("/token").permitAll()
                                                      .requestMatchers("/").permitAll()
                                                      .requestMatchers("/swagger-ui.html/**", "/swagger-ui/**").permitAll()
                                                      .requestMatchers("/v3/api-docs/**").permitAll()
                                                      .requestMatchers("/test").authenticated());

        return http.build();
    }
}
