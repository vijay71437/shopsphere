package com.shopsphere.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.security.interfaces.RSAPublicKey;

@Configuration
public class SecurityConfig {

    @Bean
    public ReactiveJwtDecoder jwtDecoder(
            RSAPublicKey publicKey,
            @Value("${jwt.issuer}") String issuer
    ) {

        NimbusReactiveJwtDecoder decoder =
                NimbusReactiveJwtDecoder
                        .withPublicKey(publicKey)
                        .build();

        decoder.setJwtValidator(
                JwtValidators.createDefaultWithIssuer(issuer)
        );

        return decoder;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http
    ) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .authorizeExchange(exchange ->
                        exchange

                                // Authentication
                                .pathMatchers(
                                        "/api/auth/**"
                                ).permitAll()

                                // Public product browsing
                                .pathMatchers(
                                        HttpMethod.GET,
                                        "/api/products/**"
                                ).permitAll()

                                // Admin product management
                                .pathMatchers(
                                        HttpMethod.POST,
                                        "/api/products/**"
                                ).hasRole("ADMIN")

                                .pathMatchers(
                                        HttpMethod.PUT,
                                        "/api/products/**"
                                ).hasRole("ADMIN")

                                .pathMatchers(
                                        HttpMethod.DELETE,
                                        "/api/products/**"
                                ).hasRole("ADMIN")

                                // Everything else requires authentication
                                .anyExchange()
                                .authenticated()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> {})
                )

                .build();
    }
}