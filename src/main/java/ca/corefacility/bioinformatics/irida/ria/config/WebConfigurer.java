package ca.corefacility.bioinformatics.irida.ria.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
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

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.ria.dialect.onsen.OnsenAttributeDialect;

import com.github.mxab.thymeleaf.extras.dataattribute.dialect.DataAttributeDialect;

/**
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "ca.corefacility.bioinformatics.irida.ria" })
@Import({ IridaApiServicesConfig.class, WebSecurityConfig.class })
public class WebConfigurer extends WebMvcConfigurerAdapter {
	public static final String SPRING_PROFILE_PRODUCTION = "prod";
	public static final String TEMPLATE_LOCATION = "/static/pages/";
	public static final String TEMPLATE_SUFFIX = ".html";
	public static final String TEMPLATE_MODE = "HTML5";
	public static final String TEMPLATE_MOBILE_PREFIX = "mobile/";
	public static final String TEMPLATE_NORMAL_PREFIX = "normal/";
	public static final long TEMPLATE_CACHE_TTL_MS = 3600000L;
	private static final Logger logger = LoggerFactory.getLogger(WebConfigurer.class);
	@Autowired
	private Environment env;

	@Bean
	public MessageSource messageSource() {
		logger.info("Configuring ReloadableResourceBundleMessageSource.");

		// TODO: Create one for each 'page'
		String[] resources = { "classpath:/i18n/login", "classpath:/i18n/global", "classpath:/i18n/dashboard",
				"classpath:/i18n/projects", "classpath:/i18n/mobile" };

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
		resolver.setPrefix(TEMPLATE_LOCATION);
		resolver.setSuffix(TEMPLATE_SUFFIX);
		resolver.setTemplateMode(TEMPLATE_MODE);

		// Set template cache timeout if in production
		// Don't cache at all if in development
		if (env.acceptsProfiles(SPRING_PROFILE_PRODUCTION)) {
			resolver.setCacheTTLMs(TEMPLATE_CACHE_TTL_MS);
		} else {
			resolver.setCacheable(false);
		}
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
		resolver.setMobilePrefix(TEMPLATE_MOBILE_PREFIX);
		resolver.setNormalPrefix(TEMPLATE_NORMAL_PREFIX);
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
		dialects.add(new DataAttributeDialect());
		dialects.add(new OnsenAttributeDialect());
		return dialects;
	}
}
