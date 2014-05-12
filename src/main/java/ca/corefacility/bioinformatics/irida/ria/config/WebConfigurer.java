package ca.corefacility.bioinformatics.irida.ria.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;

/**
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "ca.corefacility.bioinformatics.irida.ria" })
@Import({ IridaApiServicesConfig.class, WebSecurityConfig.class, ThymeleafConfiguration.class })
public class WebConfigurer extends WebMvcConfigurerAdapter {
	public static final String SPRING_PROFILE_PRODUCTION = "prod";

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// CSS: default location "/static/styles" during development and
		// production.
		registry.addResourceHandler("/styles/**").addResourceLocations("/static/styles/");
		registry.addResourceHandler("/scripts/**").addResourceLocations("/static/scripts/");
		registry.addResourceHandler("/bower_components/**").addResourceLocations("/bower_components/");
	}
}
