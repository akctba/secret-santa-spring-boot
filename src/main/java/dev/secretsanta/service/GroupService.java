package dev.secretsanta.service;

import dev.secretsanta.model.ApiModels.CreateGroupRequest;
import dev.secretsanta.model.ApiModels.Group;
import dev.secretsanta.model.ApiModels.ParticipantRequest;
import dev.secretsanta.model.ApiModels.User;
import dev.secretsanta.repository.InMemorySecretSantaRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
    private final InMemorySecretSantaRepository repository;

    public GroupService(InMemorySecretSantaRepository repository) {
        this.repository = repository;
    }

    public Group createGroup(CreateGroupRequest request) {
        return repository.createGroup(request);
    }

    public Group getGroup(String groupId) {
        return repository.findGroup(groupId);
    }

    public ParticipantRequest addParticipant(String groupId, ParticipantRequest request) {
        return repository.addParticipant(groupId, request.userId());
    }

    public List<User> listParticipants(String groupId, long requesterUserId) {
        return repository.listParticipants(groupId, requesterUserId);
    }

    public void runDraw(String groupId) {
        repository.runDraw(groupId);
    }

    public User getSecretFriend(String groupId, long requesterUserId) {
        return repository.getSecretFriend(groupId, requesterUserId);
    }
}
