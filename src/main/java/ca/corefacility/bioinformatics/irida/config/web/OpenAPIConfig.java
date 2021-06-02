package ca.corefacility.bioinformatics.irida.config.web;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for IRIDA REST API documentation.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "org.springdoc" })
@Import({ org.springdoc.core.SpringDocConfiguration.class, org.springdoc.webmvc.core.SpringDocWebMvcConfiguration.class,
		org.springdoc.core.SwaggerUiConfigProperties.class, org.springdoc.core.SwaggerUiOAuthProperties.class,
		org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.class })
@SecurityScheme(name = "oauth2", type = SecuritySchemeType.OAUTH2, flows = @OAuthFlows(authorizationCode = @OAuthFlow(authorizationUrl = "/api/oauth/authorize", tokenUrl = "/api/oauth/token", scopes = {
		@OAuthScope(name = "read", description = "This is the read scope."),
		@OAuthScope(name = "write", description = "This is the write scope.") }), password = @OAuthFlow(tokenUrl = "/api/oauth/token")))
@OpenAPIDefinition(info = @Info(title = "IRIDA REST API", version = "${irida.version}", description = "The IRIDA REST API follows a standard output format, regardless of the resource being accessed. Resources can be accessed as an individual resource or as part of a resource collection.", contact = @Contact(name = "the IRIDA Team", email = "IRIDA-mail@sfu.ca")), tags = {
		@Tag(name = "api", description = "Access to API details"),
		@Tag(name = "analysisSubmissions", description = "Everything about analysis submissions"),
		@Tag(name = "projects", description = "Everything about projects"),
		@Tag(name = "samples", description = "Everything about samples"),
		@Tag(name = "sequencingrun", description = "Everything about sequencing runs"),
		@Tag(name = "users", description = "Everything about users") }, security = @SecurityRequirement(name = "oauth2", scopes = {
		"read", "write" }))
public class OpenAPIConfig implements WebMvcConfigurer {
}
