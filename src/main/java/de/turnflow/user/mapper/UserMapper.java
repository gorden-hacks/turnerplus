package de.turnflow.user.mapper;

import de.turnflow.user.dto.UserDto;
import de.turnflow.user.entity.UserAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "member.id", target = "memberId")
    @Mapping(target = "roles", expression = "java(mapRoles(entity))")
    UserDto toDto(UserAccount entity);

    default Set<String> mapRoles(UserAccount user) {
        return user.getRoles()
                .stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }
}