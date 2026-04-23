package de.turnerplus.member;

import de.turnerplus.common.exception.NotFoundException;
import de.turnerplus.member.dto.CreateMemberRequest;
import de.turnerplus.member.dto.MemberDto;
import de.turnerplus.member.dto.UpdateMemberRequest;
import de.turnerplus.member.entity.Member;
import de.turnerplus.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    public List<MemberDto> findAll() {
        return memberMapper.toDtoList(memberRepository.findAll());
    }

    public MemberDto findById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mitglied nicht gefunden: " + id));
        return memberMapper.toDto(member);
    }

    public MemberDto create(CreateMemberRequest request) {
        Member member = memberMapper.toEntity(request);
        Member saved = memberRepository.save(member);
        return memberMapper.toDto(saved);
    }

    public MemberDto update(Long id, UpdateMemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mitglied nicht gefunden: " + id));

        memberMapper.update(request, member);

        if (request.getActive() != null) {
            member.setActive(request.getActive());
        }

        Member saved = memberRepository.save(member);
        return memberMapper.toDto(saved);
    }
}