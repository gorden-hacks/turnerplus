package de.turnflow.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TurnFlow API")
                        .version("v1")
                        .description("REST API für Trainingsverwaltung, Anmeldung und Vereinsorganisation."))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addTagsItem(new Tag()
                        .name("Auth")
                        .description("Login, aktueller Benutzer und Authentifizierung"))
                .addTagsItem(new Tag()
                        .name("Members")
                        .description("Mitgliederverwaltung"))
                .addTagsItem(new Tag()
                        .name("Training Groups")
                        .description("Trainingsgruppen und Gruppenberechtigungen"))
                .addTagsItem(new Tag()
                        .name("Training Sessions")
                        .description("Trainingseinheiten"))
                .addTagsItem(new Tag()
                        .name("Registrations")
                        .description("An- und Abmeldungen zu Trainingseinheiten"))
                .addTagsItem(new Tag()
                        .name("Users")
                        .description("Benutzerkonten, Rollen und Login-Zuordnung"))
                .addTagsItem(new Tag()
                        .name("Training Groups")
                        .description("Trainingsgruppen, Mitglieder-Berechtigungen und Trainer-Zuordnungen"))
                .addTagsItem(new Tag()
                        .name("Training Sessions")
                        .description("Trainingseinheiten, Filter und Kalenderansicht"));
    }
}