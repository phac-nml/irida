package ca.corefacility.bioinformatics.irida.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.web.context.request.SessionScope;

@Configuration
public class IridaWebTestScopeConfig {
	@Bean
	public CustomScopeConfigurer customScopeConfigurer() {
		CustomScopeConfigurer customScopeConfigurer = new CustomScopeConfigurer();
		SimpleThreadScope session = new SimpleThreadScope();
		Map<String, Object> scopes = new HashMap<>();
		scopes.put("session", session);
		customScopeConfigurer.setScopes(scopes);
		return customScopeConfigurer;
	}
}
