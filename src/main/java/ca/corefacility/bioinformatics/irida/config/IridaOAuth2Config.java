package ca.corefacility.bioinformatics.irida.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@ImportResource(value={"classpath:ca/corefacility/bioinformatics/irida/config/oauth-config.xml"})
public class IridaOAuth2Config {
	@Autowired
	ClientDetailsService clientDetailsService;
	
	@Bean
	public DefaultTokenServices tokenServices(){	
		DefaultTokenServices services = new DefaultTokenServices();
		services.setTokenStore(tokenStore());
		services.setSupportRefreshToken(true);
		services.setClientDetailsService(clientDetailsService);
		return services;
	}

	@Bean
	public TokenStore tokenStore(){
		TokenStore store = new InMemoryTokenStore();
		return store;
	}
	
	@Bean
	public ClientDetailsUserDetailsService clientDetailsUserDetailsService(){
		ClientDetailsUserDetailsService clientDetailsUserDetailsService = new ClientDetailsUserDetailsService(clientDetailsService);
		return clientDetailsUserDetailsService;
	}
}
