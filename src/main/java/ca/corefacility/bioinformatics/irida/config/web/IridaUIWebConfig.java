package ca.corefacility.bioinformatics.irida.config.web;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.extras.conditionalcomments.dialect.ConditionalCommentsDialect;
import org.thymeleaf.extras.springsecurity3.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import ca.corefacility.bioinformatics.irida.config.security.IridaApiSecurityConfig;
import ca.corefacility.bioinformatics.irida.ria.config.AnalyticsHandlerInterceptor;
import ca.corefacility.bioinformatics.irida.ria.config.WebEmailConfig;
import ca.corefacility.bioinformatics.irida.ria.dialects.FontAwesomeDialect;

import com.github.dandelion.datatables.thymeleaf.dialect.DataTablesDialect;
import com.github.dandelion.thymeleaf.dialect.DandelionDialect;
import com.github.mxab.thymeleaf.extras.dataattribute.dialect.DataAttributeDialect;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import nz.net.ultraq.thymeleaf.LayoutDialect;

/**
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "ca.corefacility.bioinformatics.irida.ria" })
@Import({WebEmailConfig.class, IridaApiSecurityConfig.class})
public class IridaUIWebConfig extends WebMvcConfigurerAdapter {
	private static final String SPRING_PROFILE_PRODUCTION = "prod";
	private static final String TEMPLATE_LOCATION = "/pages/";
	private static final String TEMPLATE_SUFFIX = ".html";
	private static final String TEMPLATE_MODE = "HTML5";
	private static final long TEMPLATE_CACHE_TTL_MS = 3600000L;
	private static final String LOCALE_CHANGE_PARAMETER = "lang";
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final String[] RESOURCE_LOCATIONS = { "classpath:/i18n/messages", "classpath:/i18n/mobile" };
	private static final Logger logger = LoggerFactory.getLogger(IridaUIWebConfig.class);
	public static final long MAX_UPLOAD_SIZE = 20971520L; // 20MB
	private final static String ANALYTICS_DIR = "/etc/irida/analytics/";

	@Autowired
	private Environment env;

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		logger.debug("Configuring LocaleChangeInterceptor");
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName(LOCALE_CHANGE_PARAMETER);
		return localeChangeInterceptor;
	}
	
	@Bean
	public AnalyticsHandlerInterceptor analyticsHandlerInterceptor() {
		Path analyticsPath = Paths.get(ANALYTICS_DIR);
		StringBuilder analytics = new StringBuilder();
		if (Files.exists(analyticsPath)) {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(analyticsPath)) {
				for (Path entry : stream) {
					List<String> lines = Files.readAllLines(entry);
					analytics.append(Joiner.on("\n").join(lines));
					analytics.append("\n");
				}
			} catch (DirectoryIteratorException ex) {
				logger.error("Error reading analytics directory: ", ex);
			} catch (IOException e) {
				logger.error("Error readin analytics file: ", e);
			}
		}
		return new AnalyticsHandlerInterceptor(analytics.toString());
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

		// Set template cache timeout if in production
		// Don't cache at all if in development
		if (!env.acceptsProfiles(SPRING_PROFILE_PRODUCTION)) {
			source.setCacheSeconds(0);
		}

		return source;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		logger.debug("Configuring Resource Handlers");
		// CSS: default location "/static/styles" during development and
		// production.
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
		registry.addResourceHandler("/public/**").addResourceLocations("/public/");
		registry.addResourceHandler("/dandelion-assets/**").addResourceLocations("/dandelion-assets/");
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/projects/templates/merge").setViewName("projects/templates/merge");
		registry.addViewController("/projects/templates/copy").setViewName("projects/templates/copy");
		registry.addViewController("/projects/templates/move").setViewName("projects/templates/move");
		registry.addViewController("/projects/templates/remove").setViewName("projects/templates/remove");
		registry.addViewController("/cart/templates/galaxy").setViewName("cart/templates/galaxy");
		registry.addViewController("/projects/templates/referenceFiles/delete").setViewName(
				"projects/templates/referenceFiles/delete");
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
			resolver.setCacheTTLMs(0L);
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
	public ViewResolver thymeleafViewResolver() {
		logger.debug("Configuring thymeleaf view resolver.");
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(templateEngine());
		viewResolver.setOrder(1);
		viewResolver.setStaticVariables(ImmutableMap.of("themePath", "themes/bootstrap/"));
		// DO NOT try to handle REST API calls.
		viewResolver.setOrder(Ordered.LOWEST_PRECEDENCE);
		return viewResolver;
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
		registry.addInterceptor(analyticsHandlerInterceptor());
	}

	/**
	 * This is to add additional Thymeleaf dialects.
	 * 
	 * @return A Set of Thymeleaf dialects.
	 */
	private Set<IDialect> additionalDialects() {
		Set<IDialect> dialects = new HashSet<>();
		dialects.add(new SpringSecurityDialect());
		dialects.add(new LayoutDialect());
		dialects.add(new ConditionalCommentsDialect());
		dialects.add(new DataAttributeDialect());
		dialects.add(new DandelionDialect());
		dialects.add(new DataTablesDialect());
		dialects.add(new FontAwesomeDialect());
		return dialects;
	}

}
