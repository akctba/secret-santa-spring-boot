package dev.secretsanta.repository;

import dev.secretsanta.exception.ApiException;
import dev.secretsanta.model.ApiModels.CreateGroupRequest;
import dev.secretsanta.model.ApiModels.Group;
import dev.secretsanta.model.ApiModels.ParticipantRequest;
import dev.secretsanta.model.ApiModels.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

@Repository
public class InMemorySecretSantaRepository {
    private record StoredUser(User user, String passwordHash) {
    }

    private record StoredGroup(Group group, List<Long> participants, Map<Long, Long> assignments) {
    }

    private final AtomicLong userIds = new AtomicLong();
    private final Map<Long, StoredUser> users = new ConcurrentHashMap<>();
    private final Map<String, StoredGroup> groups = new ConcurrentHashMap<>();

    public synchronized User createUser(String userName, String email, String passwordHash) {
        boolean exists = users.values().stream().anyMatch(user -> user.user().userEmail().equalsIgnoreCase(email));
        if (exists) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email is already registered");
        }
        long userId = userIds.incrementAndGet();
        User user = new User(userId, userName, email, null, null);
        users.put(userId, new StoredUser(user, passwordHash));
        return user;
    }

    public User findUser(long userId) {
        StoredUser user = users.get(userId);
        if (user == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "User not found");
        }
        return user.user();
    }

    public User findUserByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.user().userEmail().equalsIgnoreCase(email))
                .map(StoredUser::user)
                .findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    }

    public Group createGroup(CreateGroupRequest request) {
        findUser(request.creatorUserId());
        String groupId = "grp_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        Group group = new Group(groupId, request.name(), request.dateCreated(), request.dateDraw(), request.creatorUserId());
        groups.put(groupId, new StoredGroup(group, new ArrayList<>(), new ConcurrentHashMap<>()));
        return group;
    }

    public Group findGroup(String groupId) {
        return requireGroup(groupId).group();
    }

    public synchronized ParticipantRequest addParticipant(String groupId, long userId) {
        findUser(userId);
        StoredGroup group = requireGroup(groupId);
        if (!group.participants().contains(userId)) {
            group.participants().add(userId);
        }
        return new ParticipantRequest(groupId, userId);
    }

    public List<User> listParticipants(String groupId, long requesterUserId) {
        StoredGroup group = requireGroup(groupId);
        boolean isCreator = group.group().creatorUserId() == requesterUserId;
        boolean isParticipant = group.participants().contains(requesterUserId);
        if (!isCreator && !isParticipant) {
            throw new ApiException(HttpStatus.FORBIDDEN, "User is not a participant of this group");
        }
        return group.participants().stream().map(this::findUser).toList();
    }

    public synchronized void runDraw(String groupId) {
        StoredGroup group = requireGroup(groupId);
        if (group.participants().size() < 2) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "At least two participants are required");
        }
        List<Long> assignments = new ArrayList<>(group.participants());
        Collections.rotate(assignments, 1);
        group.assignments().clear();
        for (int index = 0; index < group.participants().size(); index++) {
            group.assignments().put(group.participants().get(index), assignments.get(index));
        }
    }

    public User getSecretFriend(String groupId, long requesterUserId) {
        StoredGroup group = requireGroup(groupId);
        if (!group.participants().contains(requesterUserId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "User is not a participant of this group");
        }
        Long friendUserId = group.assignments().get(requesterUserId);
        if (friendUserId == null) {
            throw new ApiException(HttpStatus.CONFLICT, "Secret friend has not been drawn yet");
        }
        return findUser(friendUserId);
    }

    public String passwordHashForEmail(String email) {
        return users.values().stream()
                .filter(user -> user.user().userEmail().equalsIgnoreCase(email))
                .map(StoredUser::passwordHash)
                .findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    }

    private StoredGroup requireGroup(String groupId) {
        StoredGroup group = groups.get(groupId);
        if (group == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Group not found");
        }
        return group;
    }
}
