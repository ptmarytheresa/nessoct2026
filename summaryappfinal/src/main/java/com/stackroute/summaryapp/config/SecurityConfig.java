package com.stackroute.summaryapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public PasswordEncoder getencoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	
	@Bean
	public UserDetailsService users(PasswordEncoder encoder) {
		UserDetails user = User.builder()
			.username("Mary")
			.password(encoder.encode("password"))
			.roles("MANAGER","USER")
			.build();
		UserDetails admin = User.builder()
			.username("Ram")
			.password(encoder.encode("pass123"))
			.roles("ADMIN")
			.build();
		return new InMemoryUserDetailsManager(user, admin);
	}
	
	
	 @Bean
	    public SecurityFilterChain securityFilterChain(
	            HttpSecurity http)
	            throws Exception {

	        http
	                .csrf(csrf -> csrf.disable())

	                .authorizeHttpRequests(auth -> auth

	                        .requestMatchers(
	                                "/swagger-ui/**",
	                                "/v3/api-docs/**",
	                                "/api/summary/**")
	                        .permitAll()

//	                        .requestMatchers(
//	                                "/api/summary/**")
//	                        .hasAnyRole(
//	                                "USER",
//	                                "MANAGER",
//	                                "ADMIN")

	                        .requestMatchers(
	                                "/api/v2/summarize",
	                                "/api/v2/summarize-json")
	                        .hasAnyRole(
	                                "USER",
	                                "MANAGER")

	                        .requestMatchers(
	                                "/api/v2/mail-draft",
	                                "/api/v2/meeting-workflow")
	                        .hasRole(
	                                "MANAGER")

	                        .requestMatchers(
	                                "/api/v2/user-history/**")
	                        .hasRole(
	                                "ADMIN")

	                        .anyRequest()
	                        .authenticated()
	                )

	                .httpBasic(
	                        Customizer.withDefaults());

	        return http.build();
	    }
}
