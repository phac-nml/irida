package ca.corefacility.bioinformatics.irida.database.changesets;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowParameter;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Liquibase update to convert the project settings for automated Assembly and SISTR checkboxes to analysis templates.
 */
public class AutomatedAnalysisToTemplate implements CustomSqlChange {

	private static final Logger logger = LoggerFactory.getLogger(AutomatedAnalysisToTemplate.class);
	private IridaWorkflowsService workflowsService;
	private DataSource dataSource;

	@Override
	public SqlStatement[] generateStatements(Database database) throws CustomChangeException {

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		IridaWorkflow assemblyWorkflow = null;
		IridaWorkflow sistrWorkflow = null;

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date today = new Date();

		//get the workflow information from the service
		try {
			assemblyWorkflow = workflowsService.getDefaultWorkflowByType(BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION);

			logger.debug("Updating automated assembly project settings");

			//get the workflow identifiers
			UUID assemblyId = assemblyWorkflow.getWorkflowIdentifier();
			//get the default parameters
			List<IridaWorkflowParameter> defaultAssemblyParams = assemblyWorkflow.getWorkflowDescription()
					.getParameters();

			//insert the assembly
			insertWorkflow(jdbcTemplate, "Automated AssemblyAnnotation",
					"Converted from automated assembly project setting on " + dateFormat.format(today), true,
					assemblyId, "p.assemble_uploads=1", defaultAssemblyParams);

		} catch (IridaWorkflowNotFoundException e) {
			logger.warn(
					"Assembly workflow not found.  Automated assemblies will not be converted to analysis templates.");
			//Note this will definitely happen in the galaxy CI tests as only SNVPhyl and a test workflow are configured.
		}

		//get the workflow information from the service
		try {
			sistrWorkflow = workflowsService.getDefaultWorkflowByType(BuiltInAnalysisTypes.SISTR_TYPING);

			logger.debug("Updating automated SISTR project settings");
			//get the workflow identifiers
			UUID sistrId = sistrWorkflow.getWorkflowIdentifier();

			//get the default params
			List<IridaWorkflowParameter> defaultSistrParams = sistrWorkflow.getWorkflowDescription()
					.getParameters();

			//insert the sistr entries without metadata
			insertWorkflow(jdbcTemplate, "Automated SISTR Typing",
					"Converted from automated SISTR typing project setting on " + dateFormat.format(today), false,
					sistrId, "p.sistr_typing_uploads='AUTO'", defaultSistrParams);
			//insert the sistr entries with metadata
			insertWorkflow(jdbcTemplate, "Automated SISTR Typing",
					"Converted from automated SISTR typing project setting on " + dateFormat.format(today), true,
					sistrId, "p.sistr_typing_uploads='AUTO_METADATA'", defaultSistrParams);
		} catch (IridaWorkflowNotFoundException e) {
			logger.warn("SISTR workflow not found.  Automated SISTR will not be converted to analysis templates.");
			//Note this will definitely happen in the galaxy CI tests as only SNVPhyl and a test workflow are configured.
		}

		return new SqlStatement[0];
	}

	private void insertWorkflow(JdbcTemplate jdbcTemplate, String name, String description, boolean updateSamples,
			UUID workflowId, String where, List<IridaWorkflowParameter> params) {
		/*
		 * we're borrowing the 'automated' flag here to mark entries we need to add params to later.  at this point
		 * there'll be nothing with a '1' in the automated flag.  we'll clear it later.
		 */

		//first get the project ids that have an automated submission
		String idSql = "SELECT p.id FROM project p WHERE " + where;
		List<Long> projectIds = jdbcTemplate.queryForList(idSql, Long.class);

		//build the params for the insert submission
		List<Object[]> queryParams = projectIds.stream()
				.map(p -> {
					return new Object[] { name, description, updateSamples, workflowId.toString(), p };
				})
				.collect(Collectors.toList());

		//then insert the submisisons for each project
		String submissionInsert = "INSERT INTO analysis_submission (DTYPE, name, analysis_description, created_date, priority, update_samples, workflow_id, submitter, submitted_project_id, automated) VALUES ('AnalysisSubmissionTemplate', ?, ?, now(), 'LOW', ?, ?, 1, ?, 1)";
		int[] updates = jdbcTemplate.batchUpdate(submissionInsert, queryParams);

		//check if we did any updates
		int update = IntStream.of(updates)
				.sum();

		//if we added anything, add the params
		if (update > 0) {

			// Insert the default params for the analysis type
			for (IridaWorkflowParameter p : params) {

				//first get the analysis submission ids we're inserting for
				String paramSelect = "SELECT a.id FROM analysis_submission a WHERE a.name=? AND a.automated=1";
				List<Long> submissionIds = jdbcTemplate.queryForList(paramSelect, Long.class, name);

				//build the argument list for the query
				List<Object[]> submissionParamArgs = submissionIds.stream()
						.map(i -> {
							return new Object[] { i, p.getName(), p.getDefaultValue() };
						})
						.collect(Collectors.toList());

				//then insert the params for each submission
				String paramInsert = "INSERT INTO analysis_submission_parameters (id, name,value) VALUES (?, ?, ?)";
				jdbcTemplate.batchUpdate(paramInsert, submissionParamArgs);
			}

			// remove the automated=1
			String removeAutomatedSql = "UPDATE analysis_submission SET automated=null WHERE DTYPE = 'AnalysisSubmissionTemplate' AND name=?";
			jdbcTemplate.update(removeAutomatedSql, name);
		} else {
			logger.debug("No automated analyeses added for " + name);
		}
	}

	@Override
	public String getConfirmationMessage() {
		return "Converted automated SISTR and AssemblyAnnotation project settings to analysis templates";
	}

	@Override
	public void setUp() throws SetupException {
		logger.info("Converting automated SISTR and AssemblyAnnotation project settings to analysis templates");
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
			this.workflowsService = applicationContext.getBean(IridaWorkflowsService.class);

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
}
