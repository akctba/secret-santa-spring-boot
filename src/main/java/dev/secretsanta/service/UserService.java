package dev.secretsanta.service;

import dev.secretsanta.exception.ApiException;
import dev.secretsanta.model.ApiModels.CreateUserRequest;
import dev.secretsanta.model.ApiModels.RefreshTokenResponse;
import dev.secretsanta.model.ApiModels.RefreshTokenRequest;
import dev.secretsanta.model.ApiModels.SigninRequest;
import dev.secretsanta.model.ApiModels.SigninResponse;
import dev.secretsanta.model.ApiModels.User;
import dev.secretsanta.repository.InMemorySecretSantaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final InMemorySecretSantaRepository repository;
    private final AuthService authService;

    public UserService(InMemorySecretSantaRepository repository, AuthService authService) {
        this.repository = repository;
        this.authService = authService;
    }

    public User createUser(CreateUserRequest request) {
        return repository.createUser(request.userName(), request.email(), authService.hashPassword(request.password()));
    }

    public SigninResponse signin(SigninRequest request) {
        User user = repository.findUserByEmail(request.email());
        String storedHash = repository.passwordHashForEmail(request.email());
        if (!authService.matches(request.password(), storedHash)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return new SigninResponse(authService.createAccessToken(user.userId()), authService.createRefreshToken(user.userId()));
    }

    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        long userId = authService.parseRefreshToken(request.refreshToken());
        return new RefreshTokenResponse(authService.createAccessToken(userId));
    }

    public User getUser(long userId) {
        return repository.findUser(userId);
    }
}
