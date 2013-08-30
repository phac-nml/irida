package ca.corefacility.bioinformatics.irida.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.SessionCreationPolicy;

import ca.corefacility.bioinformatics.irida.config.IridaApiSecurityConfig;

@Configuration
@EnableWebSecurity
// DO NOT WANT. For some reason, even though we import the
// IridaApiSecurityConfig below (that already has @EnableGlobalMethodSecurity),
// we must add the annotation again to this configuration class. That doesn't
// allow us to turn method security on in the API project, then use the API
// project configuration elsewhere.
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Import(IridaApiSecurityConfig.class)
public class IridaRestApiSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private AuthenticationManager authenticationManager;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeUrls().anyRequest().fullyAuthenticated().and().httpBasic().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.stateless);
	}
}
