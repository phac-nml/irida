package ca.corefacility.bioinformatics.irida.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@ComponentScan("ca.corefacility.bioinformatics.irida.config.oauth")
public class IridaOAuth2Config {

	@Bean
	public DefaultTokenServices tokenServices(ClientDetailsService clientDetails) {
		DefaultTokenServices services = new DefaultTokenServices();
		services.setTokenStore(tokenStore());
		services.setSupportRefreshToken(true);
		services.setClientDetailsService(clientDetails);
		return services;
	}

	@Bean
	public TokenStore tokenStore() {
		TokenStore store = new InMemoryTokenStore();
		return store;
	}

	@Bean
	public ClientDetailsUserDetailsService clientDetailsUserDetailsService(ClientDetailsService clientDetails) {
		ClientDetailsUserDetailsService clientDetailsUserDetailsService = new ClientDetailsUserDetailsService(
				clientDetails);

		return clientDetailsUserDetailsService;
	}




}
