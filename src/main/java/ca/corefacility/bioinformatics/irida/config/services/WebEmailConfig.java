package ca.corefacility.bioinformatics.irida.config.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
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
@Import({ IridaApiPropertyPlaceholderConfig.class })
public class WebEmailConfig {

	private static final String MAIL_TEMPLATE_PREFIX = "/mail/";
	private static final String TEMPLATE_SUFFIX = ".html";
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
	public ConfigurableJavaMailSender javaMailSender() {
		ConfigurableJavaMailSenderImpl sender = new ConfigurableJavaMailSenderImpl();
		sender.setHost(host);
		sender.setProtocol(protocol);
		sender.setUsername(username);
		return sender;
	}

	/**
	 * Configure the template resolver
	 * @return A ClassLoaderTemplateResolver
	 */
	public ClassLoaderTemplateResolver classLoaderTemplateResolver() {
		ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
		classLoaderTemplateResolver.setPrefix(MAIL_TEMPLATE_PREFIX);
		classLoaderTemplateResolver.setSuffix(TEMPLATE_SUFFIX);
		classLoaderTemplateResolver.setTemplateMode(TemplateMode.HTML);
		classLoaderTemplateResolver.setCharacterEncoding(CHARACER_ENCODING);
		return classLoaderTemplateResolver;
	}

	@Bean
	public SpringTemplateEngine emailTemplateEngine() {
		SpringTemplateEngine emailTemplateEngine = new SpringTemplateEngine();
		emailTemplateEngine.addTemplateResolver(classLoaderTemplateResolver());
		return emailTemplateEngine;
	}

	/**
	 * An extension of {@link JavaMailSender} that allows checking to see if
	 * it's configured.
	 * 
	 */
	public interface ConfigurableJavaMailSender extends JavaMailSender {
		/**
		 * Check to see if the mail server has been configured correctly.
		 * 
		 * @return {@link Boolean#TRUE} if configured, {@link Boolean#FALSE}
		 *         if unconfigured.
		 */
		public Boolean isConfigured();
	}

	/**
	 * Implementation of {@link ConfigurableJavaMailSender}.
	 *
	 */
	public static class ConfigurableJavaMailSenderImpl extends JavaMailSenderImpl implements ConfigurableJavaMailSender {
		
		private static final Logger logger = LoggerFactory.getLogger(ConfigurableJavaMailSenderImpl.class);
				
		private static final String UNCONFIGURED_HOST_VALUE = "YOUR_MAIL_HOST";
		private static final String UNCONFIGURED_PROTOCOL_VALUE = "YOUR_MAIL_PROTOCOL (usually SMTP)";
		private static final String UNCONFIGURED_USERNAME_VALUE = "YOUR_MAIL_NAME (the name that the e-mail is coming from)";
				
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Boolean isConfigured() {
			if (UNCONFIGURED_HOST_VALUE.equals(getHost())) {
				logger.warn("E-mail host is not configured, unable to send e-mails.");
				return Boolean.FALSE;
			} else if (UNCONFIGURED_PROTOCOL_VALUE.equals(getProtocol())) {
				logger.warn("E-mail protocol is not configured, unable to send e-mails.");
				return Boolean.FALSE;
			} else if (UNCONFIGURED_USERNAME_VALUE.equals(getUsername())) {
				logger.warn("E-mail username is not configured, unable to send e-mails.");
				return Boolean.FALSE;
			}
		
			return Boolean.TRUE;
		}

	}
}
