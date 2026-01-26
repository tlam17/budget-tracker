package com.tylerlam.budgettracker.budget_tracker_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String privateKey;
    private String publicKey;
    private String issuer;
    private long accessTokenExpiration;
    private long refreshTokenExpiration;
}
