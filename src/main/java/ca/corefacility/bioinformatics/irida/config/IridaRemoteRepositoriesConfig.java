package ca.corefacility.bioinformatics.irida.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.corefacility.bioinformatics.irida.repositories.remote.oltu.OAuthTokenRestTemplate;
import ca.corefacility.bioinformatics.irida.repositories.remote.oltu.OltuProjectRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.token.InMemoryTokenRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.token.TokenRepository;

@Configuration
public class IridaRemoteRepositoriesConfig {
	
	@Bean
	public TokenRepository tokenRepository(){
		return new InMemoryTokenRepository();
	}
	
	@Bean
	public OAuthTokenRestTemplate oAuthTokenRestTemplate(){
		OAuthTokenRestTemplate oAuthTokenRestTemplate = new OAuthTokenRestTemplate(tokenRepository());

		return oAuthTokenRestTemplate;
	}

	
	@Bean
	public OltuProjectRemoteRepository oltuProjectRemoteRepository(){
		return new OltuProjectRemoteRepository(oAuthTokenRestTemplate());
	}
}
	
