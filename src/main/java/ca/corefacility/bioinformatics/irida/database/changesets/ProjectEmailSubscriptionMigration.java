package ca.corefacility.bioinformatics.irida.database.changesets;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.model.subscription.ProjectSubscription;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;

/**
 * Liquibase update to create {@link ProjectSubscription} entries for every project that a user has access to.
 */
public class ProjectEmailSubscriptionMigration implements CustomTaskChange {

	private static final Logger logger = LoggerFactory.getLogger(ProjectEmailSubscriptionMigration.class);
	private DataSource dataSource;

	@Override
	public void execute(Database database) throws CustomChangeException {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		// Get all the current project & group level access
		List<ProjectEmailSubscriptionMigration.ProjectSubscriptionResult> projectSubscriptionResults = jdbcTemplate.query(
				"SELECT project_id, user_id, email_subscription FROM project_user UNION SELECT p.project_id, m.user_id, 0 from user_group_member m LEFT JOIN user_group_project p ON m.group_id = p.user_group_id;",
				(rs, rowNum) -> {
					return new ProjectSubscriptionResult(rs.getLong("project_id"), rs.getLong("user_id"),
							rs.getBoolean("email_subscription"));
				});

		// Create a new ProjectSubscription entry for each project & group level access
		for (ProjectEmailSubscriptionMigration.ProjectSubscriptionResult projectSubscriptionResult : projectSubscriptionResults) {
			jdbcTemplate.update(
					"INSERT INTO project_subscription (project_id, user_id, email_subscription, created_date) VALUES (?,?,?,?)",
					projectSubscriptionResult.projectId, projectSubscriptionResult.userId,
					projectSubscriptionResult.emailSubscription, new Date());
		}
	}

	@Override
	public String getConfirmationMessage() {
		return "Successfully created project subscriptions for every project a user has access to";
	}

	@Override
	public void setUp() throws SetupException {
		logger.info("Starting to create project subscriptions for every project a user has access to");
	}

	@Override
	public void setFileOpener(ResourceAccessor resourceAccessor) {
		logger.info("The resource accessor is of type [" + resourceAccessor.getClass() + "]");
		final ApplicationContext applicationContext;
		if (resourceAccessor instanceof IridaApiJdbcDataSourceConfig.ApplicationContextAwareSpringLiquibase.ApplicationContextSpringResourceOpener) {
			applicationContext = ((IridaApiJdbcDataSourceConfig.ApplicationContextAwareSpringLiquibase.ApplicationContextSpringResourceOpener) resourceAccessor).getApplicationContext();
		} else {
			applicationContext = null;
		}

		if (applicationContext != null) {
			logger.info("We're running inside of a spring instance, getting the existing application context.");
			this.dataSource = applicationContext.getBean(DataSource.class);
		} else {
			logger.error(
					"This changeset *must* be run from a servlet container as it requires access to Spring's application context.");
			throw new IllegalStateException(
					"This changeset *must* be run from a servlet container as it requires access to Spring's application context.");
		}
	}

	@Override
	public ValidationErrors validate(Database database) {
		return null;
	}

	/**
	 * Convenience class to store project subscription results of a query
	 */
	private class ProjectSubscriptionResult {
		Long projectId;
		Long userId;
		boolean emailSubscription;

		public ProjectSubscriptionResult(Long projectId, Long userId, boolean emailSubscription) {
			this.projectId = projectId;
			this.userId = userId;
			this.emailSubscription = emailSubscription;
		}
	}
}
