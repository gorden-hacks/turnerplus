package de.turnflow.registration.mapper;


import de.turnflow.registration.dto.RegistrationDto;
import de.turnflow.registration.entity.Registration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {

    @Mapping(source = "trainingSession.id", target = "trainingSessionId")
    @Mapping(source = "member.id", target = "memberId")
    @Mapping(source = "member.firstName", target = "memberFirstName")
    @Mapping(source = "member.lastName", target = "memberLastName")
    RegistrationDto toDto(Registration registration);
}