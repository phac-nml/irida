package ca.corefacility.bioinformatics.irida.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.SessionCreationPolicy;

import ca.corefacility.bioinformatics.irida.config.IridaApiSecurityConfig;

@Configuration
@EnableWebSecurity
@Import(IridaApiSecurityConfig.class)
public class IridaRestApiSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private AuthenticationManager authenticationManager;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeUrls().anyRequest().fullyAuthenticated().and().httpBasic().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.stateless);
	}

	@Override
	protected void registerAuthentication(AuthenticationManagerBuilder auth) {
		auth.parentAuthenticationManager(authenticationManager);
	}
}
