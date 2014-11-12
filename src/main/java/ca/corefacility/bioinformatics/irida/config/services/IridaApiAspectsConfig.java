package ca.corefacility.bioinformatics.irida.config.services;

import javax.validation.Validator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import ca.corefacility.bioinformatics.irida.validators.ValidMethodParametersAspect;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class IridaApiAspectsConfig {

	@Bean
	public ValidMethodParametersAspect validMethodsParametersAspect(Validator validator) {
		return new ValidMethodParametersAspect(validator);
	}
}
