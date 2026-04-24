package de.turnflow.member.mapper;


import de.turnflow.member.dto.CreateMemberRequest;
import de.turnflow.member.dto.MemberDto;
import de.turnflow.member.dto.UpdateMemberRequest;
import de.turnflow.member.entity.Member;
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