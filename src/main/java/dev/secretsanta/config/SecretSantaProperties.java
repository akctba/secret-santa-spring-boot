package dev.secretsanta.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "secret-santa")
public class SecretSantaProperties {
    private final Auth auth = new Auth();

    public Auth getAuth() {
        return auth;
    }

    public static class Auth {
        @NotBlank
        private String issuer = "secret-santa-api";
        @NotBlank
        private String secret = "change-me";
        @Positive
        private long accessTokenMinutes = 60;
        @Positive
        private long refreshTokenDays = 30;

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getAccessTokenMinutes() {
            return accessTokenMinutes;
        }

        public void setAccessTokenMinutes(long accessTokenMinutes) {
            this.accessTokenMinutes = accessTokenMinutes;
        }

        public long getRefreshTokenDays() {
            return refreshTokenDays;
        }

        public void setRefreshTokenDays(long refreshTokenDays) {
            this.refreshTokenDays = refreshTokenDays;
        }
    }
}
