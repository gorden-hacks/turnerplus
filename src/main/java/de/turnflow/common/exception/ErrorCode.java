package de.turnflow.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    NOT_FOUND("COMMON_NOT_FOUND", "error.common.notFound", HttpStatus.NOT_FOUND),
    BUSINESS_RULE_VIOLATION("BUSINESS_RULE_VIOLATION", "error.business.ruleViolation", HttpStatus.BAD_REQUEST),

    MEMBER_NOT_FOUND("MEMBER_NOT_FOUND", "error.member.notFound", HttpStatus.NOT_FOUND),
    MEMBER_ALREADY_REGISTERED("MEMBER_ALREADY_REGISTERED", "error.registration.memberAlreadyRegistered", HttpStatus.CONFLICT),
    MEMBER_INACTIVE("MEMBER_INACTIVE", "error.member.inactive", HttpStatus.BAD_REQUEST),
    MEMBER_NOT_ALLOWED_FOR_GROUP("MEMBER_NOT_ALLOWED_FOR_GROUP", "error.member.notAllowedForGroup", HttpStatus.BAD_REQUEST),
    MEMBER_GROUP_PERMISSION_ALREADY_EXISTS("MEMBER_GROUP_PERMISSION_ALREADY_EXISTS", "error.memberGroupPermission.alreadyExists", HttpStatus.CONFLICT),
    MEMBER_GROUP_PERMISSION_NOT_FOUND("MEMBER_GROUP_PERMISSION_NOT_FOUND", "error.memberGroupPermission.notFound", HttpStatus.NOT_FOUND),

    TRAINING_SESSION_NOT_FOUND("TRAINING_SESSION_NOT_FOUND", "error.trainingSession.notFound", HttpStatus.NOT_FOUND),
    TRAINING_SESSION_NOT_OPEN("TRAINING_SESSION_NOT_OPEN", "error.trainingSession.notOpen", HttpStatus.BAD_REQUEST),
    TRAINING_SESSION_FULL("TRAINING_SESSION_FULL", "error.trainingSession.full", HttpStatus.BAD_REQUEST),
    TRAINING_SESSION_IN_PAST("TRAINING_SESSION_IN_PAST", "error.trainingSession.inPast", HttpStatus.BAD_REQUEST),
    TRAINING_GROUP_ALREADY_EXISTS("TRAINING_GROUP_ALREADY_EXISTS", "error.trainingGroup.alreadyExists", HttpStatus.CONFLICT),
    TRAINING_GROUP_NOT_FOUND("TRAINING_GROUP_NOT_FOUND", "error.trainingGroup.notFound", HttpStatus.NOT_FOUND),
    TRAINING_SESSION_INVALID_TIME_RANGE("TRAINING_SESSION_INVALID_TIME_RANGE", "error.trainingSession.invalidTimeRange", HttpStatus.BAD_REQUEST),
    TRAINING_SESSION_INVALID_REGISTRATION_DEADLINE("TRAINING_SESSION_INVALID_REGISTRATION_DEADLINE", "error.trainingSession.invalidRegistrationDeadline", HttpStatus.BAD_REQUEST),
    TRAINER_ASSIGNMENT_NOT_FOUND("TRAINER_ASSIGNMENT_NOT_FOUND", "error.trainer.assignment.notFound", HttpStatus.NOT_FOUND),
    TRAINER_GROUP_ASSIGNMENT_ALREADY_EXISTS("TRAINER_GROUP_ASSIGNMENT_ALREADY_EXISTS", "error.trainer.assignment.alreadyExists", HttpStatus.CONFLICT),

    REGISTRATION_NOT_FOUND("REGISTRATION_NOT_FOUND", "error.registration.notFound", HttpStatus.NOT_FOUND),
    REGISTRATION_DEADLINE_EXPIRED("REGISTRATION_DEADLINE_EXPIRED", "error.registration.deadlineExpired", HttpStatus.BAD_REQUEST),
    UNREGISTER_AFTER_SESSION_END_NOT_ALLOWED("UNREGISTER_AFTER_SESSION_END_NOT_ALLOWED", "error.registration.unregisterAfterSessionEndNotAllowed", HttpStatus.BAD_REQUEST),

    USER_NOT_FOUND("USER_NOT_FOUND", "error.user.notFound", HttpStatus.NOT_FOUND),
    USER_MISSING_REQUIRED_ROLE("USER_MISSING_REQUIRED_ROLE", "error.user.missingRequiredRole", HttpStatus.BAD_REQUEST),
    USERNAME_ALREADY_EXISTS("USERNAME_ALREADY_EXISTS", "error.user.usernameAlreadyExists", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "error.user.emailAlreadyExists", HttpStatus.CONFLICT),
    INVALID_ROLE("INVALID_ROLE", "error.role.invalid", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String messageKey;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String messageKey, HttpStatus httpStatus) {
        this.code = code;
        this.messageKey = messageKey;
        this.httpStatus = httpStatus;
    }
}