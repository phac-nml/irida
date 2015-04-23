package ca.corefacility.bioinformatics.irida.example.oauthClient.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;


@Configuration
@ComponentScan(basePackages="ca.corefacility.bioinformatics.irida.example.oauthClient")
@EnableWebMvc
public class MvcConfiguration extends WebMvcConfigurerAdapter{
	private static final String TEMPLATE_MODE = "HTML5";
	private static final String TEMPLATE_PREFIX = "/WEB-INF/pages/";
	private static final String TEMPLATE_SUFFIX = ".html";
	private static final int TEMPLATE_ORDER = 1;
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	}

	/**
	 * Create the {@link ThymeleafViewResolver}
	 * 
	 * @return {@link ViewResolver}
	 */
	@Bean
	public ViewResolver thymeleafViewResolver() {
		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
		resolver.setTemplateEngine(templateEngine());
		return resolver;
	}
	
	/**
	 * Thymeleaf needs a template engine, get a {@link SpringTemplateEngine}
	 * 
	 * @return {@link SpringTemplateEngine}
	 */
	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setTemplateResolver(templateResolver());
		return engine;
	}
	
	/**
	 * Create a new {@link ServletContextTemplateResolver} and set defaults.
	 * 
	 * @return {@link ServletContextTemplateResolver}
	 */
	@Bean
	public ServletContextTemplateResolver templateResolver() {
		ServletContextTemplateResolver resolver = new ServletContextTemplateResolver();
		resolver.setPrefix(TEMPLATE_PREFIX);
		resolver.setSuffix(TEMPLATE_SUFFIX);
		resolver.setTemplateMode(TEMPLATE_MODE);
		resolver.setOrder(TEMPLATE_ORDER);
		resolver.setCacheable(false);

		return resolver;
	}
	
	/**
	 * Message source for internationalization
	 * @return
	 */
	@Bean
	public MessageSource messageSource() {
		String[] resources = { "classpath:/i18n/demo" };

		ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
		source.setBasenames(resources);
		source.setDefaultEncoding("UTF-8");
		return source;
	}
	
}
