package com.example.userservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception{
        //if any http request come, permit all the request but disable cors() and csrf()
        http.authorizeHttpRequests((requests) -> {
            try{
                requests.anyRequest().permitAll()
                        .and().cors().disable()
                        .csrf().disable();
            }catch(Exception ex){
                throw new RuntimeException(ex);
            }
        });
        return http.build();
    }
}
