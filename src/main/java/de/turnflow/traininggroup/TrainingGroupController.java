package de.turnflow.traininggroup;

import de.turnflow.traininggroup.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Training Groups")
@RestController
@RequestMapping("/api/v1/training-groups")
@RequiredArgsConstructor
public class TrainingGroupController {

    private final TrainingGroupService trainingGroupService;

    @Operation(summary = "Alle Trainingsgruppen abrufen")
    @GetMapping
    public List<TrainingGroupDto> getAll() {
        return trainingGroupService.findAll();
    }

    @Operation(summary = "Trainingsgruppe nach ID abrufen")
    @GetMapping("/{id}")
    public TrainingGroupDto getById(@PathVariable Long id) {
        return trainingGroupService.findById(id);
    }

    @Operation(summary = "Trainingsgruppe erstellen")
    @PostMapping
    public TrainingGroupDto create(@Valid @RequestBody CreateTrainingGroupRequest request) {
        return trainingGroupService.create(request);
    }

    @Operation(summary = "Trainingsgruppe aktualisieren")
    @PutMapping("/{id}")
    public TrainingGroupDto update(
            @PathVariable Long id,
            @RequestBody UpdateTrainingGroupRequest request
    ) {
        return trainingGroupService.update(id, request);
    }

    @Operation(summary = "Mitglied für Trainingsgruppe berechtigen")
    @PostMapping("/permissions")
    public MemberGroupPermissionDto addMemberPermission(
            @Valid @RequestBody CreateMemberGroupPermissionRequest request
    ) {
        return trainingGroupService.addMemberPermission(request);
    }

    @Operation(summary = "Gruppenberechtigung entfernen")
    @DeleteMapping("/permissions/{permissionId}")
    public void removeMemberPermission(@PathVariable Long permissionId) {
        trainingGroupService.removeMemberPermission(permissionId);
    }

    @Operation(summary = "Berechtigungen einer Trainingsgruppe abrufen")
    @GetMapping("/{groupId}/permissions")
    public List<MemberGroupPermissionDto> getPermissionsByGroup(@PathVariable Long groupId) {
        return trainingGroupService.findPermissionsByGroup(groupId);
    }

    @Operation(summary = "Berechtigungen eines Mitglieds abrufen")
    @GetMapping("/permissions/by-member/{memberId}")
    public List<MemberGroupPermissionDto> getPermissionsByMember(@PathVariable Long memberId) {
        return trainingGroupService.findPermissionsByMember(memberId);
    }

    @Operation(summary = "Trainer einer Trainingsgruppe zuordnen")
    @PostMapping("/trainer-assignments")
    public TrainerGroupAssignmentDto addTrainerAssignment(
            @Valid @RequestBody CreateTrainerGroupAssignmentRequest request
    ) {
        return trainingGroupService.addTrainerAssignment(request);
    }

    @Operation(summary = "Trainer-Zuordnung entfernen")
    @DeleteMapping("/trainer-assignments/{assignmentId}")
    public void removeTrainerAssignment(@PathVariable Long assignmentId) {
        trainingGroupService.removeTrainerAssignment(assignmentId);
    }

    @Operation(summary = "Trainer-Zuordnungen einer Trainingsgruppe abrufen")
    @GetMapping("/{groupId}/trainer-assignments")
    public List<TrainerGroupAssignmentDto> getTrainerAssignmentsByGroup(@PathVariable Long groupId) {
        return trainingGroupService.findTrainerAssignmentsByGroup(groupId);
    }

    @Operation(summary = "Trainer-Zuordnungen eines Users abrufen")
    @GetMapping("/trainer-assignments/by-user/{userId}")
    public List<TrainerGroupAssignmentDto> getTrainerAssignmentsByUser(@PathVariable Long userId) {
        return trainingGroupService.findTrainerAssignmentsByUser(userId);
    }
}