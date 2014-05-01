package ca.corefacility.bioinformatics.irida.ria.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "ca.corefacility.bioinformatics.irida.ria" })
public class WebConfigurer extends WebMvcConfigurerAdapter {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/css/**").addResourceLocations("/static/css/");
	}
}
