package ca.corefacility.bioinformatics.irida.web.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.vote.ScopeVoter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * Configuration for web security using OAuth2
 * @author "Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>"
 *
 */
@Configuration
@EnableWebSecurity
@ImportResource("classpath:oauth2-web-config.xml")
public class IridaRestApiSecurityConfig extends WebSecurityConfigurerAdapter {
	@Bean
	public AuthenticationEntryPoint oauthAuthenticationEntryPoint(){
		OAuth2AuthenticationEntryPoint entryPoint = new OAuth2AuthenticationEntryPoint();
		return entryPoint;
	}
	
	@Bean
	public AccessDeniedHandler oauthAccessDeniedHandler(){
		OAuth2AccessDeniedHandler handler = new OAuth2AccessDeniedHandler();
		return handler;
	}
	
	
	@Bean
	public AuthenticationEntryPoint clientAuthenticationEntryPoint(){
		OAuth2AuthenticationEntryPoint clientAuthenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
		clientAuthenticationEntryPoint.setRealmName("NmlIrida/client");
		clientAuthenticationEntryPoint.setTypeName("Basic");
		return clientAuthenticationEntryPoint;
	}
	
	@Bean
	public ClientCredentialsTokenEndpointFilter clientCredentialsTokenEndpointFilter(AuthenticationManager clientAuthenticationManager){
		ClientCredentialsTokenEndpointFilter clientCredentialsTokenEndpointFilter = new ClientCredentialsTokenEndpointFilter();
		clientCredentialsTokenEndpointFilter.setAuthenticationManager(clientAuthenticationManager);
		return clientCredentialsTokenEndpointFilter;
	}
	
	@Bean
	public AccessDecisionManager accessDecisionManager(){
		@SuppressWarnings("rawtypes")
		List<AccessDecisionVoter> voters = new ArrayList<>();
		voters.add(new ScopeVoter());
		voters.add(new RoleVoter());
		voters.add(new AuthenticatedVoter());
		UnanimousBased unanimousBased = new UnanimousBased(voters);
		return unanimousBased;
	}
}
