package de.turnerplus.member;

import de.turnerplus.member.dto.CreateMemberRequest;
import de.turnerplus.member.dto.MemberDto;
import de.turnerplus.member.dto.UpdateMemberRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public List<MemberDto> getAll() {
        return memberService.findAll();
    }

    @GetMapping("/{id}")
    public MemberDto getById(@PathVariable Long id) {
        return memberService.findById(id);
    }

    @PostMapping
    public MemberDto create(@Valid @RequestBody CreateMemberRequest request) {
        return memberService.create(request);
    }

    @PutMapping("/{id}")
    public MemberDto update(@PathVariable Long id,
                            @Valid @RequestBody UpdateMemberRequest request) {
        return memberService.update(id, request);
    }
}