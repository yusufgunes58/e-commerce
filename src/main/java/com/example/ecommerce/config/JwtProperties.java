package com.example.ecommerce.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private String secret;

    //  second !
    private long accessTokenExpiration;
    private long refreshTokenExpiration;
    private long maxSessionDuration;

    // think  Duration instead long.
}
