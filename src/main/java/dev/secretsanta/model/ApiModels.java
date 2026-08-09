package dev.secretsanta.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public final class ApiModels {
    private ApiModels() {
    }

    public record CreateUserRequest(
            @JsonProperty("user_name") @NotBlank String userName,
            @Email @NotBlank String email,
            @NotBlank String password) {
    }

    public record SigninRequest(@Email @NotBlank String email, @NotBlank String password) {
    }

    public record RefreshTokenRequest(@JsonProperty("refresh_token") @NotBlank String refreshToken) {
    }

    public record SigninResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("refresh_token") String refreshToken) {
    }

    public record RefreshTokenResponse(@JsonProperty("access_token") String accessToken) {
    }

    public record User(
            @JsonProperty("user_id") long userId,
            @JsonProperty("user_name") String userName,
            @JsonProperty("user_email") String userEmail,
            String gender,
            @JsonProperty("date_of_birth") Instant dateOfBirth) {
    }

    public record CreateGroupRequest(
            @NotBlank String name,
            @JsonProperty("date_created") @NotNull Instant dateCreated,
            @JsonProperty("date_draw") @NotNull Instant dateDraw,
            @JsonProperty("creator_user_id") @Min(1) long creatorUserId) {
    }

    public record Group(
            @JsonProperty("group_id") String groupId,
            String name,
            @JsonProperty("date_created") Instant dateCreated,
            @JsonProperty("date_draw") Instant dateDraw,
            @JsonProperty("creator_user_id") long creatorUserId) {
    }

    public record ParticipantRequest(
            @JsonProperty("group_id") @NotBlank String groupId,
            @JsonProperty("user_id") @Min(1) long userId) {
    }
}
