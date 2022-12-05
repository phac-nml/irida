package ca.corefacility.bioinformatics.irida.config.web;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import ca.corefacility.bioinformatics.irida.jackson2.mixin.SampleMixin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.web.controller.api.json.PathJson;
import ca.corefacility.bioinformatics.irida.web.controller.api.json.TimestampJson;
import ca.corefacility.bioinformatics.irida.web.spring.view.*;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableMap;

/**
 * Configuration for IRIDA REST API.
 */
@Configuration
@ComponentScan(basePackages = { "ca.corefacility.bioinformatics.irida.web.controller.api" })
public class IridaRestApiWebConfig implements WebMvcConfigurer {

	/**
	 * named constant for allowing unlimited upload sizes.
	 */
	public static final Long UNLIMITED_UPLOAD_SIZE = -1L;

	@Value("${file.upload.max_size}")
	private Long MAX_UPLOAD_SIZE = UNLIMITED_UPLOAD_SIZE;

	public static final int MAX_IN_MEMORY_SIZE = 1048576; // 1MB

	private static final Logger logger = LoggerFactory.getLogger(IridaRestApiWebConfig.class);

	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();

		resolver.setMaxInMemorySize(MAX_IN_MEMORY_SIZE);
		resolver.setMaxUploadSize(MAX_UPLOAD_SIZE);

		return resolver;
	}

	@Bean
	public ViewResolver apiViewResolver(ContentNegotiationManager contentNegotiationManager) {
		logger.debug("Configuring REST API view resolver.");
		ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
		resolver.setDefaultViews(defaultViews());
		resolver.setContentNegotiationManager(contentNegotiationManager);
		resolver.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return resolver;
	}

	private List<View> defaultViews() {
		List<View> views = new ArrayList<>();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		// MappingJackson2JsonView and @ResponseBody cannot be used together.
		// This setting, serializes the object like @ResponseBody
		// and removes the outer attribute key.
		jsonView.setExtractValueFromSingleKeyModel(true);
		jsonView.setPrettyPrint(true);

		// add support for serializing Path data
		SimpleModule module = new SimpleModule();
		module.addSerializer(Path.class, new PathJson.PathSerializer());
		jsonView.getObjectMapper().registerModule(module);

		// Add sample mixin to ignore default sequencing object and default genome assembly
		jsonView.getObjectMapper().addMixIn(Sample.class, SampleMixin.class);

		// java.util.date fields (i.e. createdDate, modifiedDate, etc) are
		// stored in the database with
		// seconds precision, but are generated at higher precision. To combat
		// this, previously the entity
		// was re-read from the database. Now we are just formatting the date
		// with 0s for milliseconds
		// portion.
		SimpleModule timestampModule = new SimpleModule();
		timestampModule.addSerializer(Date.class, new TimestampJson.TimestampSerializer());
		jsonView.getObjectMapper().registerModule(timestampModule);

		views.add(jsonView);

		views.add(new FastaView());
		views.add(new FastqView());
		views.add(new GenbankView());
		views.add(new NewickFileView());
		views.add(new CSVView());
		return views;
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		Map<String, MediaType> mediaTypes = ImmutableMap.of("json", MediaType.APPLICATION_JSON, "fasta",
				MediaType.valueOf("application/fasta"), "fastq", MediaType.valueOf("application/fastq"), "gbk",
				MediaType.valueOf("application/genbank"));
		configurer.ignoreAcceptHeader(false).defaultContentType(MediaType.APPLICATION_JSON).mediaTypes(mediaTypes);
	}
}
