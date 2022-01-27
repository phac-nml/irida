package ca.corefacility.bioinformatics.irida.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.LocalHostUriTemplateHandler;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.RemoteApiUtilities;
import ca.corefacility.bioinformatics.irida.ria.web.oauth.OltuAuthorizationController;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;

@Configuration
@Profile("it")
public class IridaIntegrationTestUriConfig {

    private LocalHostUriTemplateHandler uriTemplateHandler;

    @Autowired
    private OltuAuthorizationController oltuAuthorizationController;

    @EventListener
    public void onServletContainerInitialized(WebServerInitializedEvent event) {
        uriTemplateHandler = new LocalHostUriTemplateHandler(event.getApplicationContext().getEnvironment());
        String baseUrl = uriTemplateHandler.getRootUri() + "/";
        AbstractPage.setBaseUrl(baseUrl);
        RemoteApiUtilities.setBaseUrl(baseUrl);
        oltuAuthorizationController.setServerBase(baseUrl);

        // Setup RestAssured
        RestAssured.requestSpecification = new RequestSpecBuilder()
				.setContentType(ContentType.JSON)
				.setAccept(ContentType.JSON)
                .setBaseUri(uriTemplateHandler.getRootUri())
				.build();
    }

    @Lazy
    @Bean
    public LocalHostUriTemplateHandler uriTemplateHandler() {
        return uriTemplateHandler;
    }
    
}
