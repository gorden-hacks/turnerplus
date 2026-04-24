package de.turnflow.traininggroup.mapper;


import de.turnflow.traininggroup.dto.MemberGroupPermissionDto;
import de.turnflow.traininggroup.entity.MemberGroupPermission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberGroupPermissionMapper {

    @Mapping(source = "member.id", target = "memberId")
    @Mapping(source = "member.firstName", target = "memberFirstName")
    @Mapping(source = "member.lastName", target = "memberLastName")
    @Mapping(source = "trainingGroup.id", target = "trainingGroupId")
    @Mapping(source = "trainingGroup.name", target = "trainingGroupName")
    MemberGroupPermissionDto toDto(MemberGroupPermission entity);
}