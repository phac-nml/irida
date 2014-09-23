package ca.corefacility.bioinformatics.irida.config.workflow;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerConfigurationException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.RemoteWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.repositories.workflow.RemoteWorkflowRepository;
import ca.corefacility.bioinformatics.irida.service.workflow.galaxy.phylogenomics.impl.RemoteWorkflowServicePhylogenomics;

import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Workflow;
import com.google.common.collect.ImmutableList;

/**
 * Configuration for loading up remote workflows.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Configuration
@Profile({ "dev", "prod", "it" })
public class RemoteWorkflowServiceConfig {

	private static final Logger logger = LoggerFactory.getLogger(RemoteWorkflowServiceConfig.class);

	private static final String WORKFLOW_ID = "galaxy.execution.workflow.phylogenomics.id";

	private static final String DEFAULT_PHYLOGENOMICS_WORKFLOW_NAME = "SNPhyl Pipeline (imported from API)";

	private static final String SEQUENCE_INPUT_LABEL = "inputSequenceLabel";
	private static final String REFERENCE_INPUT_LABEL = "inputReferenceLabel";
	private static final String DEFAULT_TREE_OUTPUT = "snp_tree.tre";
	private static final String DEFAULT_MATRIX_OUTPUT = "snp_matrix.tsv";
	private static final String DEFAULT_SNP_TABLE_OUTPUT = "snp_table.tsv";

	@Autowired
	private Environment environment;

	@Autowired
	private RemoteWorkflowRepository remoteWorkflowRepository;

	@Autowired
	private GalaxyWorkflowService galaxyWorkflowService;

	/**
	 * Create an instance of {@link RemoteWorkflow} and automatically install it
	 * to the database (if necessary). This bean is intended to be run *before*
	 * actual execution of a servlet, but *after* the database has been
	 * populated. This duplicates what's done in
	 * {@link InstallRemoteWorkflowPhylogenomics}.
	 * 
	 * @param remoteWorkflowRepository
	 *            the workflow repository used to save the remote workflow.
	 * @return an instance of the phylogenomics workflow.
	 */
	@Bean
	@Profile({ "dev", "prod" })
	@DependsOn("springLiquibase")
	public RemoteWorkflowPhylogenomics remoteWorkflow(final RemoteWorkflowRepository remoteWorkflowRepository,
			final WorkflowsClient workflowClient, final GalaxyWorkflowService galaxyWorkflowService) {
		// figure out what the workflow ID is given the credentials that we have
		// in the workflow client
		try {
			final Optional<Workflow> workflowCheck = workflowClient.getWorkflows().stream()
					.filter(w -> w.getName().equals(DEFAULT_PHYLOGENOMICS_WORKFLOW_NAME)).findAny();

			if (!workflowCheck.isPresent()) {
				logger.warn("Could not auto-configure SNVPhyl Workflow in Galaxy using workflow name ["
						+ DEFAULT_PHYLOGENOMICS_WORKFLOW_NAME + "]. Is your Galaxy configured correctly?");
				logger.warn("These are the pipeline names I *could* find:");
				for (final Workflow w : workflowClient.getWorkflows()) {
					logger.warn("\t" + w.getName());
				}
				return null;
			}

			// get the workflow checksum from
			// `GalaxyWorkflowService#getWorkflowChecksum`
			final String workflowId = workflowCheck.get().getId();
			final String workflowChecksum = galaxyWorkflowService.getWorkflowChecksum(workflowId);

			// assume that we're using the default names for input labels and
			// output
			// file names.
			final RemoteWorkflowPhylogenomics phylogenomicsWorkflow = new RemoteWorkflowPhylogenomics(workflowId,
					workflowChecksum, SEQUENCE_INPUT_LABEL, REFERENCE_INPUT_LABEL, DEFAULT_TREE_OUTPUT,
					DEFAULT_MATRIX_OUTPUT, DEFAULT_SNP_TABLE_OUTPUT);

			// return the instance that we persisted (even if nobody is going to
			// consume it).
			if (remoteWorkflowRepository.exists(workflowId)) {
				logger.info("Remote Phylogenomics Worklfow with id [" + workflowId
						+ "] already configured, using existing configuration.");
				return (RemoteWorkflowPhylogenomics) remoteWorkflowRepository.findOne(workflowId);
			} else {
				logger.info("Configuring Remote Phylogenomics Workflow with id [" + workflowId + "]");

				// TODO: This is a really ugly hack. We *need* to figure out how
				// to get the auditing provider to allow us to have non-audited
				// references in the graph so we don't have to audit what are
				// effectively read-only tables.
				final User u = new User();
				u.setUsername("admin");
				SecurityContextHolder.getContext().setAuthentication(
						new AnonymousAuthenticationToken("workflow-setup", u, ImmutableList.of(Role.ROLE_ADMIN)));
				final RemoteWorkflowPhylogenomics saved = remoteWorkflowRepository.save(phylogenomicsWorkflow);
				SecurityContextHolder.clearContext();

				return saved;
			}
		} catch (Exception e) {
			logger.error("Could not connect to Galaxy. Not attempting to auto-configure workflow.");
		}

		return null;
	}

	/**
	 * @return A RemoteWorkflowServicePhylogenomics with a correctly implemented
	 *         workflow.
	 * @throws WorkflowException
	 */
	@Lazy
	@Bean
	public RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomics() throws WorkflowException {
		RemoteWorkflowPhylogenomics remoteWorkflow = null;

		try {
			String workflowId = getProperty("workflowId", WORKFLOW_ID);

			if (remoteWorkflowRepository.exists(workflowId)) {
				remoteWorkflow = remoteWorkflowRepository.getByType(workflowId, RemoteWorkflowPhylogenomics.class);
			}
		} catch (ExecutionManagerConfigurationException e) {
			logger.error(e.getMessage());
		}

		return new RemoteWorkflowServicePhylogenomics(remoteWorkflow);
	}

	/**
	 * Gets a properties value given it's name.
	 * 
	 * @param propertyType
	 *            The type of property (used for error messages).
	 * @param propertyName
	 *            The name of the property.
	 * @return The value of the property.
	 * @throws ExecutionManagerConfigurationException
	 *             If there was no corresponding string for the given property.
	 */
	private String getProperty(String propertyType, String propertyName) throws ExecutionManagerConfigurationException {
		String propertyValue = environment.getProperty(propertyName);

		if (propertyValue == null) {
			throw new ExecutionManagerConfigurationException("Missing " + propertyType, propertyName);
		} else {
			return propertyValue;
		}
	}
}
