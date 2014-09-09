package ca.corefacility.bioinformatics.irida.ria.config;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuth2Configuration {

	@Bean
	public OAuthClient oAuthClient() {
		return new OAuthClient(new URLConnectionClient());
	}
}
