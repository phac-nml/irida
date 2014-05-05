package ca.corefacility.bioinformatics.irida.ria.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import java.util.Arrays;

/**
 * Replace JSP's with Thymeleaf templating.
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Configuration
public class ThymeleafConfiguration {
	private static final String TEMPLATE_MODE = "HTML5";
	private static final String TEMPLATE_PREFIX = "/static/";
	private static final String TEMPLATE_SUFFIX = ".html";
	private static final Long TEMPLATE_CACHE_TIME = 3600000L;
	private static final int TEMPLATE_ORDER = 1;
	private static final boolean TEMPLATE_NOT_CACHEABLE = false;

	@Autowired
	private Environment env;

	/**
	 * Add Thymeleaf as the view resolver.
	 * 
	 * @return {@link ViewResolver}
	 */
	@Bean
	public ViewResolver viewResolver() {
		ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
		resolver.setViewResolvers(Arrays.asList(thymeleafViewResolver()));
		return resolver;
	}

	/**
	 * Create a new {@link ServletContextTemplateResolver} and set defaults.
	 * 
	 * @return {@link ServletContextTemplateResolver}
	 */
	@Bean
	@Description("Thymeleaf template resolver serving HTML 5")
	public ServletContextTemplateResolver templateResolver() {
		ServletContextTemplateResolver resolver = new ServletContextTemplateResolver();
		resolver.setPrefix(TEMPLATE_PREFIX);
		resolver.setSuffix(TEMPLATE_SUFFIX);
		resolver.setTemplateMode(TEMPLATE_MODE);
		resolver.setOrder(TEMPLATE_ORDER);

		// Determine the spring profile that is being run.
		// If it is in development we do not want the templates cached
		if (env.acceptsProfiles(WebConfigurer.SPRING_PROFILE_PRODUCTION)) {
			resolver.setCacheTTLMs(TEMPLATE_CACHE_TIME);
		} else {
			resolver.setCacheable(TEMPLATE_NOT_CACHEABLE);
		}
		return resolver;
	}

	/**
	 * Thymeleaf needs a template engine, get a {@link SpringTemplateEngine}
	 * 
	 * @return {@link SpringTemplateEngine}
	 */
	@Bean
	@Description("Thymeleaf template engine with Spring integration")
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setTemplateResolver(templateResolver());
		return engine;
	}

	/**
	 * Create the actual {@link ThymeleafViewResolver}
	 * 
	 * @return {@link ViewResolver}
	 */
	@Bean
	@Description("Thymeleaf view resolver")
	public ViewResolver thymeleafViewResolver() {
		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
		resolver.setTemplateEngine(templateEngine());
		return resolver;
	}

	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasename("i18n/messages");
		return source;
	}
}
