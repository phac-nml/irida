package ca.corefacility.bioinformatics.irida.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

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
		//List<HttpMessageConverter<?>> messageConverters = oAuthTokenRestTemplate.getMessageConverters();
		//messageConverters.add(mappingJacksonHttpMessageConverter());
		//oAuthTokenRestTemplate.setMessageConverters(messageConverters);
		return oAuthTokenRestTemplate;
	}
	
	@Bean 
	public HttpMessageConverter<?> mappingJacksonHttpMessageConverter(){
		//MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
		MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter = new MappingJacksonHttpMessageConverter();
		List<MediaType> mediaTypes = new ArrayList<>();
		mediaTypes.add(MediaType.APPLICATION_JSON);
		mappingJacksonHttpMessageConverter.setSupportedMediaTypes(mediaTypes);
		return mappingJacksonHttpMessageConverter;
	}
	
	@Bean
	public OltuProjectRemoteRepository oltuProjectRemoteRepository(){
		return new OltuProjectRemoteRepository(oAuthTokenRestTemplate());
	}
}
	
