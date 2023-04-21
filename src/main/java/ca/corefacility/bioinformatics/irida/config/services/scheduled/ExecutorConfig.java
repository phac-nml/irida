package ca.corefacility.bioinformatics.irida.config.services.scheduled;

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.concurrent.DelegatingSecurityContextScheduledExecutorService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.sql.DataSource;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Configuration for a task executor for running scheduled tasks.
 */
@Configuration
public class ExecutorConfig {

	@Autowired
	private UserService userService;

	@Autowired
	private DataSource dataSource;

	@Value("${irida.scheduled.threads}")
	private int threadCount = 2;

	/**
	 * Task executor for running in regular production or development modes
	 *
	 * @return a TaskExecutor
	 */
	@Bean(name = "scheduledTaskExecutor")
	@Profile({ "prod", "dev", "ncbi", "analysis", "sync", "processing", "email", "web" })
	@DependsOn("springLiquibase")
	public Executor productionExecutor() {
		ScheduledExecutorService delegateExecutor = Executors.newScheduledThreadPool(threadCount);

		SecurityContext schedulerContext = createSchedulerSecurityContext();
		return new DelegatingSecurityContextScheduledExecutorService(delegateExecutor, schedulerContext);
	}

	/**
	 * Task executor for running in test modes.  This adds a test admin user ot the database before executing
	 *
	 * @return a TaskExecutor
	 */
	@Bean(name = "scheduledTaskExecutor")
	@Profile({ "it", "test" })
	@DependsOn("springLiquibase")
	public Executor testExecutor() {
		addAdminUser();

		ScheduledExecutorService delegateExecutor = Executors.newScheduledThreadPool(threadCount);

		SecurityContext schedulerContext = createSchedulerSecurityContext();
		return new DelegatingSecurityContextScheduledExecutorService(delegateExecutor, schedulerContext);
	}

	/**
	 * Creates a security context object for the scheduled tasks.
	 *
	 * @return A {@link SecurityContext} for the scheduled tasks.
	 */
	private SecurityContext createSchedulerSecurityContext() {

		SecurityContext context = SecurityContextHolder.createEmptyContext();

		Authentication anonymousToken = new AnonymousAuthenticationToken("nobody", "nobody",
				ImmutableList.of(Role.ROLE_ANONYMOUS));

		Authentication oldAuthentication = SecurityContextHolder.getContext().getAuthentication();
		SecurityContextHolder.getContext().setAuthentication(anonymousToken);
		User admin = userService.getUserByUsername("admin");
		SecurityContextHolder.getContext().setAuthentication(oldAuthentication);

		Authentication adminAuthentication = new PreAuthenticatedAuthenticationToken(admin, null,
				Lists.newArrayList(Role.ROLE_ADMIN));

		context.setAuthentication(adminAuthentication);

		return context;
	}

	/**
	 * Add an administrative user to the database for test purposes
	 */
	private void addAdminUser() {

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		//check if admin user exists.  If not add one
		if (jdbcTemplate.queryForList("SELECT userName FROM user WHERE userName='admin' and user_type='TYPE_LOCAL'").isEmpty()) {
			String userInsertSql = "insert into user (createdDate, credentialsNonExpired, email, enabled, firstName, lastName, password, phoneNumber, userName, system_role, user_type) values (now(),1,'admin@irida.ca',1,'admin','admin','xxxx','0000','admin','ROLE_ADMIN', 'TYPE_LOCAL')";
			jdbcTemplate.update(userInsertSql);
		}
	}
}
