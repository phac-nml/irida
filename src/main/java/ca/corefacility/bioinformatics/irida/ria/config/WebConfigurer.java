package ca.corefacility.bioinformatics.irida.ria.config;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.ria.thymeleaf.dialect.onsen.OnsenDialect;
import com.github.mxab.thymeleaf.extras.dataattribute.dialect.DataAttributeDialect;
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
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.extras.springsecurity3.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "ca.corefacility.bioinformatics.irida.ria" })
@Import({ IridaApiServicesConfig.class, WebSecurityConfig.class })
public class WebConfigurer extends WebMvcConfigurerAdapter {
	public static final String SPRING_PROFILE_PRODUCTION = "prod";
	public static final String TEMPLATE_LOCATION = "/pages/";
	public static final String TEMPLATE_SUFFIX = ".html";
	public static final String TEMPLATE_MODE = "HTML5";
	public static final String TEMPLATE_MOBILE_PREFIX = "mobile/";
	public static final String TEMPLATE_NORMAL_PREFIX = "normal/";
	public static final long TEMPLATE_CACHE_TTL_MS = 3600000L;
	public static final String LOCALE_CHANGE_PARAMETER = "lang";
	public static final String DEFAULT_ENCODING = "UTF-8";
	public static final String[] RESOURCE_LOCATIONS = {"classpath:/i18n/messages", "classpath:/i18n/mobile"};
	private static final Logger logger = LoggerFactory.getLogger(WebConfigurer.class);
	@Autowired
	private Environment env;

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		logger.debug("Configuring LocaleChangeInterceptor");
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName(LOCALE_CHANGE_PARAMETER);
		return localeChangeInterceptor;
	}

	@Bean(name = "localeResolver")
	public LocaleResolver localeResolver() {
		logger.debug("Configuring LocaleResolver");
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(Locale.ENGLISH);
		return slr;
	}

	@Bean
	public MessageSource messageSource() {
		logger.info("Configuring ReloadableResourceBundleMessageSource.");

		ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
		source.setBasenames(RESOURCE_LOCATIONS);
		source.setFallbackToSystemLocale(false);
		source.setDefaultEncoding(DEFAULT_ENCODING);
		return source;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		logger.debug("Configuring Resource Handlers");
		// CSS: default location "/static/styles" during development and
		// production.
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
		registry.addResourceHandler("/public/**").addResourceLocations("/public/");
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
		logger.debug("Configuring Template Resolvers.");
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
		logger.debug("Configuring SpringTemplateEngine");
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setTemplateResolver(templateResolver());
		engine.setAdditionalDialects(additionalDialects());
		return engine;
	}

	@Bean
	public LiteDeviceDelegatingViewResolver liteDeviceDelegatingViewResolver() {
		logger.debug("Configuring LiteDeviceDelegatingViewResolver");
		ThymeleafViewResolver delegate = new ThymeleafViewResolver();
		delegate.setTemplateEngine(templateEngine());
		delegate.setOrder(1);
		LiteDeviceDelegatingViewResolver resolver = new LiteDeviceDelegatingViewResolver(delegate);
		resolver.setMobilePrefix(TEMPLATE_MOBILE_PREFIX);
		resolver.setNormalPrefix(TEMPLATE_NORMAL_PREFIX);
		return resolver;
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		logger.debug("configureDefaultServletHandling");
		configurer.enable();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		logger.debug("Adding Interceptors to the Registry");
		registry.addInterceptor(localeChangeInterceptor());
		registry.addInterceptor(deviceResolverHandlerInterceptor());
		registry.addInterceptor(sitePreferenceHandlerInterceptor());
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(sitePreferenceHandlerMethodArgumentResolver());
	}

	/**
	 * This is to add additional Thymeleaf dialects.
	 * 
	 * @return A Set of thymeleaf dialects.
	 */
	private Set<IDialect> additionalDialects() {
		Set<IDialect> dialects = new HashSet<>();
		dialects.add(new SpringSecurityDialect());
		dialects.add(new OnsenDialect());
		dialects.add(new DataAttributeDialect());
		return dialects;
	}
}
