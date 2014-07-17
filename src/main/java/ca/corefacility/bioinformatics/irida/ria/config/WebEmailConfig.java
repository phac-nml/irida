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
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
@Configuration
@PropertySource(value = { "classpath:configuration.properties" }, ignoreResourceNotFound=false)
public class WebPropertyPlaceholderConfig {

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
	
	public ClassLoaderTemplateResolver classLoaderTemplateResolver(){
		ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
		classLoaderTemplateResolver.setPrefix("/mail/");
		classLoaderTemplateResolver.setTemplateMode("VALIDXHTML");
		classLoaderTemplateResolver.setCharacterEncoding("UTF-8");
		return classLoaderTemplateResolver;
	}
	
	@Bean
	public SpringTemplateEngine emailTemplateEngine(){
		SpringTemplateEngine emailTemplateEngine = new SpringTemplateEngine();
		emailTemplateEngine.addTemplateResolver(classLoaderTemplateResolver());
		return emailTemplateEngine;
	}
	
	/**
	 *  <bean id="emailTemplateResolver" class="org.thymeleaf.templateresolver.ClassLoaderTemplateResolver">
        <property name="prefix" value="mail/"/>
        <property name="templateMode" value="VALIDXHTML"/>
        <property name="characterEncoding" value="UTF-8"/>
        <property name="order" value="1"/>
    	</bean>
	 */

	/*
	 * <bean id="mailSender"
	 * class="org.springframework.mail.javamail.JavaMailSenderImpl"> <property
	 * name="host" value="${mail.server.host}"/> <property name="protocol"
	 * value="${mail.server.protocol}"/> <property name="username"
	 * value="${mail.server.username}"/> </bean>
	 */
}
