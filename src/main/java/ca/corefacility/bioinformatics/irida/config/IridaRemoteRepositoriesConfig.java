package ca.corefacility.bioinformatics.irida.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.corefacility.bioinformatics.irida.repositories.remote.oltu.OAuthTokenRestTemplate;
import ca.corefacility.bioinformatics.irida.repositories.remote.oltu.OltuProjectRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

@Configuration
public class IridaRemoteRepositoriesConfig {
	
	@Bean
	public OAuthTokenRestTemplate oAuthTokenRestTemplate(RemoteAPITokenService tokenService){
		OAuthTokenRestTemplate oAuthTokenRestTemplate = new OAuthTokenRestTemplate(tokenService);

		return oAuthTokenRestTemplate;
	}

	
	@Bean
	public OltuProjectRemoteRepository oltuProjectRemoteRepository(OAuthTokenRestTemplate restTemplate){
		return new OltuProjectRemoteRepository(restTemplate);
	}
}
	
