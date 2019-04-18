package ca.corefacility.bioinformatics.irida.database.changesets;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowParameter;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import com.google.common.collect.Lists;
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
import java.util.List;
import java.util.UUID;

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

		//get the workflow information from the service
		try {
			assemblyWorkflow = workflowsService.getDefaultWorkflowByType(BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION);
		} catch (IridaWorkflowNotFoundException e) {
			logger.warn(
					"Assembly workflow not found.  Automated assemblies will not be converted to analysis templates.");
			//Note this will definitely happen in the galaxy CI tests as only SNVPhyl and a test workflow are configured.
		}

		//get the workflow information from the service
		try {
			sistrWorkflow = workflowsService.getDefaultWorkflowByType(BuiltInAnalysisTypes.SISTR_TYPING);
		} catch (IridaWorkflowNotFoundException e) {
			logger.warn("SISTR workflow not found.  Automated SISTR will not be converted to analysis templates.");
			//Note this will definitely happen in the galaxy CI tests as only SNVPhyl and a test workflow are configured.
		}

		//update assemblies
		if (assemblyWorkflow != null) {
			logger.debug("Upadting automated assembly project settings");

			//get the workflow identifiers
			UUID assemblyId = assemblyWorkflow.getWorkflowIdentifier();
			//get the default parameters
			List<IridaWorkflowParameter> defaultAssemblyParams = assemblyWorkflow.getWorkflowDescription()
					.getParameters();

			//insert the assembly
			insertWorkflow(jdbcTemplate, "Automated AssemblyAnnotation", true, assemblyId, "p.assemble_uploads=1",
					defaultAssemblyParams);

		}

		//update SISTR
		if (sistrWorkflow != null) {
			logger.debug("Upadting automated SISTR project settings");
			//get the workflow identifiers
			UUID sistrId = sistrWorkflow.getWorkflowIdentifier();

			//get the default params
			List<IridaWorkflowParameter> defaultSistrParams = sistrWorkflow.getWorkflowDescription()
					.getParameters();

			//insert the sistr entries without metadata
			insertWorkflow(jdbcTemplate, "Automated SISTR Typing", false, sistrId, "p.sistr_typing_uploads='AUTO'",
					defaultSistrParams);
			//insert the sistr entries with metadata
			insertWorkflow(jdbcTemplate, "Automated SISTR Typing", true, sistrId,
					"p.sistr_typing_uploads='AUTO_METADATA'", defaultSistrParams);
		}

		return new SqlStatement[0];
	}

	private void insertWorkflow(JdbcTemplate jdbcTemplate, String name, boolean updateSamples, UUID workflowId,
			String where, List<IridaWorkflowParameter> params) {
		/*
		 * we're borrowing the 'automated' flag here to mark entries we need to add params to later.  at this point
		 * there'll be nothing with a '1' in the automated flag.  we'll clear it later.
		 */

		int updateSampleBit = updateSamples ? 1 : 0;

		String assemblyInsert =
				"INSERT INTO analysis_submission (DTYPE, name, created_date, priority, update_samples, workflow_id, submitter, submitted_project_id, automated) select 'AnalysisSubmissionTemplate', '"
						+ name + "', now(), 'LOW', " + updateSampleBit + ", '" + workflowId.toString()
						+ "', 1, p.id, 1 FROM project p WHERE " + where;

		logger.debug("Inserting analysis submissions");
		int update = jdbcTemplate.update(assemblyInsert);

		logger.debug("Added " + update + " submissions");

		//if we added anything, add the params
		if (update > 0) {
			// Insert the default params for the analysis type
			for (IridaWorkflowParameter p : params) {
				String assemblyParamInsert =
						"INSERT INTO analysis_submission_parameters (id, name, value) SELECT a.id, '" + p.getName()
								+ "', '" + p.getDefaultValue()
								+ "' FROM analysis_submission a where a.name=? AND a.automated=1";

				logger.debug("Inserting param " + name);
				jdbcTemplate.update(assemblyParamInsert, name);
			}

			logger.debug("Removing automated flag");
			// remove the automated=1
			String removeAutomatedSql = "UPDATE analysis_submission SET automated=null WHERE DTYPE = 'AnalysisSubmissionTemplate' AND name=?";
			jdbcTemplate.update(removeAutomatedSql, name);
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
