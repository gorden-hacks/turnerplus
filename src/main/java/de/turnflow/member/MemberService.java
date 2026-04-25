package de.turnflow.member;

import de.turnflow.common.exception.ErrorCode;
import de.turnflow.common.exception.NotFoundException;
import de.turnflow.member.dto.CreateMemberRequest;
import de.turnflow.member.dto.MemberDto;
import de.turnflow.member.dto.UpdateMemberRequest;
import de.turnflow.member.entity.Member;
import de.turnflow.member.mapper.MemberMapper;
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
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND, id));
        return memberMapper.toDto(member);
    }

    public MemberDto create(CreateMemberRequest request) {
        Member member = memberMapper.toEntity(request);
        Member saved = memberRepository.save(member);
        return memberMapper.toDto(saved);
    }

    public MemberDto update(Long id, UpdateMemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND, id));

        memberMapper.update(request, member);

        if (request.getActive() != null) {
            member.setActive(request.getActive());
        }

        Member saved = memberRepository.save(member);
        return memberMapper.toDto(saved);
    }
}