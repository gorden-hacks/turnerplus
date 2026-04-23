package de.turnerplus.member.mapper;


import de.turnerplus.member.dto.CreateMemberRequest;
import de.turnerplus.member.dto.MemberDto;
import de.turnerplus.member.dto.UpdateMemberRequest;
import de.turnerplus.member.entity.Member;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberDto toDto(Member entity);

    List<MemberDto> toDtoList(List<Member> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "photoUrl", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Member toEntity(CreateMemberRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "photoUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void update(UpdateMemberRequest request, @MappingTarget Member entity);
}