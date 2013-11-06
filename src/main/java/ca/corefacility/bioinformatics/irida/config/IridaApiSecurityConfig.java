package ca.corefacility.bioinformatics.irida.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.security.IgnoreExpiredCredentialsForPasswordChangeChecker;
import ca.corefacility.bioinformatics.irida.security.permissions.BasePermission;
import ca.corefacility.bioinformatics.irida.security.permissions.IridaPermissionEvaluator;
import ca.corefacility.bioinformatics.irida.security.permissions.ReadProjectPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.ReadSamplePermission;
import ca.corefacility.bioinformatics.irida.security.permissions.ReadSequenceFilePermission;
import ca.corefacility.bioinformatics.irida.security.permissions.UpdateUserPermission;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class IridaApiSecurityConfig extends GlobalMethodSecurityConfiguration {

	private static final String[] ROLE_HIERARCHIES = new String[] { "ROLE_ADMIN > ROLE_MANAGER",
			"ROLE_MANAGER > ROLE_USER" };

	private static final String ROLE_HIERARCHY = StringUtils.join(ROLE_HIERARCHIES, "\n");

	@Autowired
	private UserRepository userRepository;

	@Autowired
	ProjectUserJoinRepository pujRepository;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userRepository).passwordEncoder(passwordEncoder());
		auth.authenticationProvider(authenticationProvider());
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userRepository);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		authenticationProvider.setPostAuthenticationChecks(postAuthenticationChecker());
		return authenticationProvider;
	}

	@Bean
	public UserDetailsChecker postAuthenticationChecker() {
		return new IgnoreExpiredCredentialsForPasswordChangeChecker();
	}

	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {
		DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
		IridaPermissionEvaluator permissionEvaluator = new IridaPermissionEvaluator(readProjectPermission(),
				readSamplePermission(), readSequenceFilePermission(), updateUserPermission());
		permissionEvaluator.init();
		handler.setPermissionEvaluator(permissionEvaluator);
		RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
		roleHierarchy.setHierarchy(ROLE_HIERARCHY);
		handler.setRoleHierarchy(roleHierarchy);
		return handler;
	}

	@Bean
	public AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public BasePermission<User> updateUserPermission() {
		return new UpdateUserPermission();
	}

	@Bean
	public BasePermission<Project> readProjectPermission() {
		return new ReadProjectPermission(userRepository, pujRepository);
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
