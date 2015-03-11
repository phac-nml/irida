package ca.corefacility.bioinformatics.irida.ria.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * Configuration class for loading properties files. This configuration source
 * looks in three places for properties:
 * 
 * <ol>
 * <li>within the package at jdbc.dev.properties,</li>
 * <li>within the package at filesystem.properties, and</li>
 * <li>on the filesystem at /etc/irida/irida.conf</li>
 * </ol>
 * 
 * 
 */
@Configuration
@PropertySource(value = { "classpath:configuration.properties", "file:/etc/irida/web.conf" }, ignoreResourceNotFound = true)
public class WebEmailConfig {

	private static final String MAIL_TEMPLATE_PREFIX = "/mail/";
	private static final String TEMPLATE_SUFFIX = ".html";
	private static final String TEMPLATE_MODE = "VALIDXHTML";
	private static final String CHARACER_ENCODING = "UTF-8";

	@Value("${mail.server.host}")
	String host;

	@Value("${mail.server.protocol}")
	String protocol;

	@Value("${mail.server.username}")
	String username;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setHost(host);
		sender.setProtocol(protocol);
		sender.setUsername(username);
		return sender;
	}

	public ClassLoaderTemplateResolver classLoaderTemplateResolver() {
		ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
		classLoaderTemplateResolver.setPrefix(MAIL_TEMPLATE_PREFIX);
		classLoaderTemplateResolver.setSuffix(TEMPLATE_SUFFIX);
		classLoaderTemplateResolver.setTemplateMode(TEMPLATE_MODE);
		classLoaderTemplateResolver.setCharacterEncoding(CHARACER_ENCODING);
		return classLoaderTemplateResolver;
	}

	@Bean
	public SpringTemplateEngine emailTemplateEngine() {
		SpringTemplateEngine emailTemplateEngine = new SpringTemplateEngine();
		emailTemplateEngine.addTemplateResolver(classLoaderTemplateResolver());
		return emailTemplateEngine;
	}
}
