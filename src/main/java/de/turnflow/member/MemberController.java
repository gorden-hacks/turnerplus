package de.turnflow.member;

import de.turnflow.member.dto.CreateMemberRequest;
import de.turnflow.member.dto.MemberDto;
import de.turnflow.member.dto.UpdateMemberRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Members")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "Alle Mitglieder abrufen")
    @GetMapping
    public List<MemberDto> getAll() {
        return memberService.findAll();
    }

    @Operation(summary = "Mitglied nach ID abrufen")
    @GetMapping("/{id}")
    public MemberDto getById(@PathVariable Long id) {
        return memberService.findById(id);
    }

    @Operation(summary = "Mitglied erstellen")
    @PostMapping
    public MemberDto create(@Valid @RequestBody CreateMemberRequest request) {
        return memberService.create(request);
    }

    @Operation(summary = "Mitglied aktualisieren")
    @PutMapping("/{id}")
    public MemberDto update(@PathVariable Long id,
                            @Valid @RequestBody UpdateMemberRequest request) {
        return memberService.update(id, request);
    }
}