package ca.corefacility.bioinformatics.irida.config.services.scheduled;

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.concurrent.DelegatingSecurityContextScheduledExecutorService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.sql.DataSource;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Config for only activating scheduled tasks in certain profiles.
 */
@Configuration
@EnableScheduling
@Import({ AnalysisScheduledTaskConfig.class, EmailScheduledTaskConfig.class, FileProcessingScheduledTaskConfig.class,
		NcbiUploadScheduledTaskConfig.class, ProjectSyncScheduledTaskConfig.class })
public class IridaScheduledTasksConfig implements SchedulingConfigurer {

	private static final Logger logger = LoggerFactory.getLogger(IridaScheduledTasksConfig.class);

	@Autowired
	private UserService userService;

	@Autowired
	private ApplicationContext applicationContext;

	@Value("${irida.scheduled.threads}")
	private int threadCount = 2;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskExecutor());
	}

	/**
	 * Builds a new Executor for scheduled tasks.
	 *
	 * @return A new Executor for scheduled tasks.
	 */
	private Executor taskExecutor() {
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
		Set<String> profiles = Sets.newHashSet(applicationContext.getEnvironment().getActiveProfiles());

		// In test profiles the admin user may not exist.  It must be there for scheduled tasks.  Adding a test admin user here
		if (profiles.contains("it") || profiles.contains("test")) {
			addAdminUser();
		}

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
		DataSource dataSource = applicationContext.getBean(DataSource.class);

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		//check if admin user exists.  If not add one
		if (jdbcTemplate.queryForList("SELECT userName FROM user WHERE userName='admin'").isEmpty()) {
			String userInsertSql = "insert into user (createdDate, credentialsNonExpired, email, enabled, firstName, lastName, password, phoneNumber, userName, system_role) values (now(),1,'admin@irida.ca',1,'admin','admin','xxxx','0000','admin','ROLE_ADMIN')";
			jdbcTemplate.update(userInsertSql);
		}
	}
}