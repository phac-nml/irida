package ca.corefacility.bioinformatics.irida.ria.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "ca.corefacility.bioinformatics.irida.ria" })
public class WebConfigurer extends WebMvcConfigurerAdapter {
	private static final String TEMPLATE_MODE = "HTML5";
	private static final String TEMPLATE_SUFFIX = ".html";

	@Bean
	public ViewResolver viewResolver() {
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setTemplateMode(TEMPLATE_MODE);
		templateResolver.setSuffix(TEMPLATE_SUFFIX);
		return templateResolver;
	}
}
