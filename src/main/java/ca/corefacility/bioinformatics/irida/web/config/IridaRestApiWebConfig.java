package ca.corefacility.bioinformatics.irida.web.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.springframework.web.servlet.view.xml.MarshallingView;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.IridaScheduledTasksConfig;
import ca.corefacility.bioinformatics.irida.web.config.processing.IridaWebMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.web.spring.view.FastaView;
import ca.corefacility.bioinformatics.irida.web.spring.view.FastqView;
import ca.corefacility.bioinformatics.irida.web.spring.view.GenbankView;

import com.google.common.collect.ImmutableMap;

/**
 * Configuration for IRIDA REST API.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * 
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"ca.corefacility.bioinformatics.irida.web.controller.api"})
@Import({ IridaApiServicesConfig.class, IridaRestApiSecurityConfig.class, IridaWebMultithreadingConfig.class,
	IridaScheduledTasksConfig.class})
public class IridaRestApiWebConfig extends WebMvcConfigurerAdapter {

	private static final long TEN_GIGABYTES = 10737418240l;
	
	private static final String TEMPLATE_MODE = "HTML5";
	private static final String TEMPLATE_PREFIX = "/WEB-INF/pages/";
	private static final String TEMPLATE_SUFFIX = ".html";
	private static final int TEMPLATE_ORDER = 1;

	@Bean
	public MultipartResolver multipartResolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setMaxUploadSize(TEN_GIGABYTES);
		return resolver;
	}

	@Bean
	public ViewResolver viewResolver(ContentNegotiationManager contentNegotiationManager) {
		ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
		resolver.setViewResolvers(Arrays.asList(thymeleafViewResolver(),internalResourceViewResolver()));
		resolver.setDefaultViews(defaultViews());
		resolver.setContentNegotiationManager(contentNegotiationManager);

		return resolver;
	}
	
	public ViewResolver internalResourceViewResolver(){
		InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
		
		return internalResourceViewResolver;
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
	 * Create the {@link ThymeleafViewResolver}
	 * 
	 * @return {@link ViewResolver}
	 */
	private ViewResolver thymeleafViewResolver() {
		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
		resolver.setTemplateEngine(templateEngine());
		return resolver;
	}

	private List<View> defaultViews() {
		List<View> views = new ArrayList<>();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		jsonView.setPrettyPrint(true);
		views.add(jsonView);
		Jaxb2Marshaller jaxb2marshaller = new Jaxb2Marshaller();
		jaxb2marshaller
				.setPackagesToScan(new String[] { "ca.corefacility.bioinformatics.irida.web.assembler.resource" });
		MarshallingView marshallingView = new MarshallingView(jaxb2marshaller);
		views.add(marshallingView);
		views.add(new FastaView());
		views.add(new FastqView());
		views.add(new GenbankView());
		return views;
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		Map<String, MediaType> mediaTypes = ImmutableMap.of("json", MediaType.APPLICATION_JSON, "xml",
				MediaType.APPLICATION_XML, "fasta", MediaType.valueOf("application/fasta"), "fastq",
				MediaType.valueOf("application/fastq"), "gbk", MediaType.valueOf("application/genbank"));
		configurer.ignoreAcceptHeader(false).defaultContentType(MediaType.APPLICATION_JSON).favorPathExtension(true)
				.mediaTypes(mediaTypes);
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("/resources/");
	}
	
	@Bean
	public MessageSource messageSource() {
		String[] resources = { "classpath:/i18n/oauth" };

		ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
		source.setBasenames(resources);
		source.setDefaultEncoding("UTF-8");
		return source;
	}
}
