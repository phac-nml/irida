package ca.corefacility.bioinformatics.irida.config.web;

import ca.corefacility.bioinformatics.irida.config.security.IridaApiSecurityConfig;
import ca.corefacility.bioinformatics.irida.config.services.WebEmailConfig;
import ca.corefacility.bioinformatics.irida.ria.config.BreadCrumbInterceptor;
import ca.corefacility.bioinformatics.irida.ria.config.GalaxySessionInterceptor;
import ca.corefacility.bioinformatics.irida.ria.config.UserSecurityInterceptor;
import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.WebpackerDialect;
import ca.corefacility.bioinformatics.irida.ria.web.sessionAttrs.Cart;
import com.github.mxab.thymeleaf.extras.dataattribute.dialect.DataAttributeDialect;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 */
@Configuration
@ComponentScan(basePackages = { "ca.corefacility.bioinformatics.irida.ria" })
@Import({ WebEmailConfig.class, IridaApiSecurityConfig.class })
public class IridaUIWebConfig implements WebMvcConfigurer, ApplicationContextAware {
	private static final String SPRING_PROFILE_PRODUCTION = "prod";
	private final static String EXTERNAL_TEMPLATE_DIRECTORY = "/etc/irida/templates/";
	private static final String INTERNAL_TEMPLATE_PREFIX = "/pages/";
	private static final String HTML_TEMPLATE_SUFFIX = ".html";
	private static final long TEMPLATE_CACHE_TTL_MS = 3600000L;
	private static final Logger logger = LoggerFactory.getLogger(IridaUIWebConfig.class);

	@Value("${locales.default}")
	private String defaultLocaleValue;

	@Autowired
	private Environment env;

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public IridaUIWebConfig() {
		super();
	}

	@Bean
	public GalaxySessionInterceptor galaxySessionInterceptor() {
		return new GalaxySessionInterceptor();
	}

	@Bean
	public BreadCrumbInterceptor breadCrumbInterceptor() {
		return new BreadCrumbInterceptor();
	}

	@Bean
	public UserSecurityInterceptor userSecurityInterceptor() {
		return new UserSecurityInterceptor();
	}

	@Bean(name = "localeResolver")
	public LocaleResolver localeResolver() {
		logger.debug("Configuring LocaleResolver");

		Locale defaultLocale = Locale.forLanguageTag(defaultLocaleValue);

		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(defaultLocale);
		return slr;
	}

	@Bean
	@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public Cart cart() {
		return new Cart();
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		logger.debug("Configuring Resource Handlers");
		// CSS: default location "/static/styles" during development and
		// production.
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
		registry.addResourceHandler("/dist/**").addResourceLocations("/dist/");
		// serve static resources for customizing pages from /etc/irida/static
		registry.addResourceHandler("/static/**").addResourceLocations("file:/etc/irida/static/");
	}

	/**
	 * Default template resolver for IRIDA. Templates can be overridden using the external template resolver below. This
	 * will look for templates in `/src/main/webapp/pages/*`
	 *
	 * @return {@link SpringResourceTemplateResolver}
	 */
	private ITemplateResolver internalTemplateResolver() {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setApplicationContext(this.applicationContext);
		resolver.setPrefix(INTERNAL_TEMPLATE_PREFIX);
		resolver.setSuffix(HTML_TEMPLATE_SUFFIX);
		resolver.setTemplateMode(TemplateMode.HTML);
		resolver.setOrder(2);
		resolver.setCheckExistence(true);

		// Set template cache timeout if in production
		// Don't cache at all if in development
		if (env.acceptsProfiles(Profiles.of(SPRING_PROFILE_PRODUCTION))) {
			resolver.setCacheTTLMs(TEMPLATE_CACHE_TTL_MS);
		} else {
			resolver.setCacheable(false);
		}
		return resolver;
	}

	/**
	 * This is to handle any templates (usually just the login page) that are overridden by and organization. The
	 * location of these files can be modified within the application.properties file.
	 *
	 * @return {@link FileTemplateResolver}
	 */
	private ITemplateResolver externalTemplateResolver() {
		FileTemplateResolver resolver = new FileTemplateResolver();
		resolver.setSuffix(HTML_TEMPLATE_SUFFIX);
		resolver.setOrder(1);
		resolver.setPrefix(EXTERNAL_TEMPLATE_DIRECTORY);
		resolver.setTemplateMode(TemplateMode.HTML);
		resolver.setCheckExistence(true);

		// Set template cache timeout if in production
		// Don't cache at all if in development
		if (env.acceptsProfiles(Profiles.of(SPRING_PROFILE_PRODUCTION))) {
			resolver.setCacheTTLMs(TEMPLATE_CACHE_TTL_MS);
		} else {
			resolver.setCacheable(false);
		}
		return resolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.addTemplateResolver(externalTemplateResolver());
		templateEngine.addTemplateResolver(internalTemplateResolver());
		templateEngine.setEnableSpringELCompiler(false);
		templateEngine.setAdditionalDialects(additionalDialects());
		return templateEngine;
	}

	@Bean
	public ThymeleafViewResolver viewResolver() {
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(templateEngine());
		viewResolver.setOrder(1);
		return viewResolver;
	}

	@Bean
	public ErrorViewResolver errorViewResolver() {
		return new ErrorViewResolver() {
			@Value("${mail.server.email}")
			private String adminEmail;

			@Override
			public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status,
					Map<String, Object> model) {

				ModelAndView modelAndView;
				if (status == HttpStatus.NOT_FOUND) {
					modelAndView = new ModelAndView("errors/not_found");
				} else if (status == HttpStatus.FORBIDDEN) {
					modelAndView = new ModelAndView("errors/access_denied");
				} else {
					modelAndView = new ModelAndView("errors/error");
				}

				modelAndView.addAllObjects(model);
				modelAndView.addObject("adminEmail", adminEmail);

				return modelAndView;
			}
		};
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		logger.debug("Adding Interceptors to the Registry");
		registry.addInterceptor(galaxySessionInterceptor());
		registry.addInterceptor(breadCrumbInterceptor());
		registry.addInterceptor(userSecurityInterceptor());
	}

	/**
	 * This is to add additional Thymeleaf dialects.
	 *
	 * @return A Set of Thymeleaf dialects.
	 */
	private Set<IDialect> additionalDialects() {
		Set<IDialect> dialects = new HashSet<>();
		dialects.add(new WebpackerDialect(!env.acceptsProfiles(Profiles.of(SPRING_PROFILE_PRODUCTION))));
		dialects.add(new SpringSecurityDialect());
		dialects.add(new LayoutDialect());
		dialects.add(new DataAttributeDialect());
		return dialects;
	}
}
