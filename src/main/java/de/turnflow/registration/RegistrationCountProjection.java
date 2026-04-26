package de.turnflow.registration;

public interface RegistrationCountProjection {

    Long getTrainingSessionId();

    long getRegisteredCount();

    long getWaitlistCount();
}