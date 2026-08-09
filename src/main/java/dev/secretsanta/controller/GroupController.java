package dev.secretsanta.controller;

import dev.secretsanta.exception.ApiException;
import dev.secretsanta.model.ApiModels.CreateGroupRequest;
import dev.secretsanta.model.ApiModels.Group;
import dev.secretsanta.model.ApiModels.ParticipantRequest;
import dev.secretsanta.model.ApiModels.User;
import dev.secretsanta.service.GroupService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/group")
public class GroupController {
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Group createGroup(@Valid @RequestBody CreateGroupRequest request) {
        return groupService.createGroup(request);
    }

    @GetMapping("/{id}")
    public Group getGroup(@PathVariable String id) {
        return groupService.getGroup(id);
    }

    @PostMapping("/{id}/participant")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipantRequest addParticipant(@PathVariable String id, @Valid @RequestBody ParticipantRequest request) {
        if (!id.equals(request.groupId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Path and body group_id must match");
        }
        return groupService.addParticipant(id, request);
    }

    @GetMapping("/{id}/participants")
    public List<User> listParticipants(@PathVariable String id, Principal principal) {
        return groupService.listParticipants(id, Long.parseLong(principal.getName()));
    }

    @PostMapping("/{id}/draw")
    @ResponseStatus(HttpStatus.OK)
    public void runDraw(@PathVariable String id) {
        groupService.runDraw(id);
    }

    @GetMapping("/{id}/friend")
    public User getSecretFriend(@PathVariable String id, Principal principal) {
        return groupService.getSecretFriend(id, Long.parseLong(principal.getName()));
    }
}
