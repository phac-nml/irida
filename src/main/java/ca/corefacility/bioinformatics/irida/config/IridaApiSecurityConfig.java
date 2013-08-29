package ca.corefacility.bioinformatics.irida.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.security.permissions.BasePermission;
import ca.corefacility.bioinformatics.irida.security.permissions.IridaPermissionEvaluator;
import ca.corefacility.bioinformatics.irida.security.permissions.ReadProjectPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.ReadSamplePermission;
import ca.corefacility.bioinformatics.irida.security.permissions.ReadSequenceFilePermission;
import ca.corefacility.bioinformatics.irida.security.permissions.UpdateUserPermission;
import ca.corefacility.bioinformatics.irida.service.UserService;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class IridaApiSecurityConfig extends GlobalMethodSecurityConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(IridaApiSecurityConfig.class);

	@Autowired
	private UserService userService;

	@Override
	protected void registerAuthentication(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
	}

	@Override
	@Bean
	protected MethodSecurityExpressionHandler expressionHandler() {
		DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
		handler.setPermissionEvaluator(permissionEvaluator());
		RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
		roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
		handler.setRoleHierarchy(roleHierarchy);
		return handler;
	}

	@Bean
	public AuthenticationManager authenticationManager() throws Exception {
		logger.debug("Returning authentication manager [" + super.authenticationManager() + "] from API.");
		return super.authenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean(initMethod = "init")
	public PermissionEvaluator permissionEvaluator() {
		IridaPermissionEvaluator permissionEvaluator = new IridaPermissionEvaluator(updateUserPermission(),
				readProjectPermission(), readSamplePermission(), readSequenceFilePermission());
		return permissionEvaluator;
	}

	@Bean
	public BasePermission<User> updateUserPermission() {
		return new UpdateUserPermission();
	}

	@Bean
	public BasePermission<Project> readProjectPermission() {
		return new ReadProjectPermission();
	}

	@Bean
	public BasePermission<Sample> readSamplePermission() {
		return new ReadSamplePermission();
	}

	@Bean
	public BasePermission<SequenceFile> readSequenceFilePermission() {
		return new ReadSequenceFilePermission();
	}
}
