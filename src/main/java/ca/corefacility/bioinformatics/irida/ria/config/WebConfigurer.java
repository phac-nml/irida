package ca.corefacility.bioinformatics.irida.ria.config;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mobile.device.DeviceResolverHandlerInterceptor;
import org.springframework.mobile.device.site.SitePreferenceHandlerInterceptor;
import org.springframework.mobile.device.site.SitePreferenceHandlerMethodArgumentResolver;
import org.springframework.mobile.device.view.LiteDeviceDelegatingViewResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.extras.springsecurity3.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "ca.corefacility.bioinformatics.irida.ria" })
@Import({ IridaApiServicesConfig.class, WebSecurityConfig.class })
public class WebConfigurer extends WebMvcConfigurerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(WebConfigurer.class);
	public static final String SPRING_PROFILE_PRODUCTION = "prod";

	@Bean
	public MessageSource messageSource() {
		logger.info("Configuring ReloadableResourceBundleMessageSource.");

		// TODO: Create one for each 'page'
		String[] resources= {"classpath:/i18n/login", "classpath:/i18n/dashboard", "classpath:/i18n/projects"};

		ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
		source.setBasenames(resources);
		source.setDefaultEncoding("UTF-8");
		return source;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		logger.debug("Configuring Resource Handlers");
		// CSS: default location "/static/styles" during development and
		// production.
		registry.addResourceHandler("/styles/**").addResourceLocations("/static/styles/");
		registry.addResourceHandler("/scripts/**").addResourceLocations("/static/scripts/");
		registry.addResourceHandler("/bower_components/**").addResourceLocations("/bower_components/");
	}

	@Bean
	public DeviceResolverHandlerInterceptor deviceResolverHandlerInterceptor() {
		return new DeviceResolverHandlerInterceptor();
	}

	@Bean
	public SitePreferenceHandlerInterceptor sitePreferenceHandlerInterceptor() {
		return new SitePreferenceHandlerInterceptor();
	}

	@Bean
	public SitePreferenceHandlerMethodArgumentResolver sitePreferenceHandlerMethodArgumentResolver() {
		return new SitePreferenceHandlerMethodArgumentResolver();
	}

	@Bean
	public ServletContextTemplateResolver templateResolver() {
		logger.debug("Creating Template Resolvers.");
		ServletContextTemplateResolver resolver = new ServletContextTemplateResolver();
		resolver.setPrefix("/static/pages/");
		resolver.setSuffix(".html");
		resolver.setTemplateMode("HTML5");

		resolver.setCacheable(false);
		return resolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setTemplateResolver(templateResolver());
		engine.setAdditionalDialects(additionalDialects());
		return engine;
	}

	@Bean
	public LiteDeviceDelegatingViewResolver liteDeviceDelegatingViewResolver() {
		ThymeleafViewResolver delegate = new ThymeleafViewResolver();
		delegate.setTemplateEngine(templateEngine());
		delegate.setOrder(1);
		LiteDeviceDelegatingViewResolver resolver = new LiteDeviceDelegatingViewResolver(delegate);
		resolver.setMobilePrefix("mobile/");
		resolver.setNormalPrefix("normal/");
		return resolver;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(deviceResolverHandlerInterceptor());
		registry.addInterceptor(sitePreferenceHandlerInterceptor());
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(sitePreferenceHandlerMethodArgumentResolver());
	}

	private Set<IDialect> additionalDialects() {
		Set<IDialect> dialects = new HashSet<>();
		dialects.add(new SpringSecurityDialect());
		return dialects;
	}
}
