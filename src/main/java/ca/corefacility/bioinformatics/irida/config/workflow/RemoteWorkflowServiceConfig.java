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

import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.RemoteWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.repositories.workflow.RemoteWorkflowRepository;
import ca.corefacility.bioinformatics.irida.service.workflow.galaxy.phylogenomics.impl.RemoteWorkflowServicePhylogenomics;

import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Workflow;

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

	private static final String DEFAULT_PHYLOGENOMICS_WORKFLOW_NAME = "SNPhyl Pipeline (imported from API)";

	private static final String SEQUENCE_INPUT_LABEL = "sequence_reads";
	private static final String REFERENCE_INPUT_LABEL = "reference";
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
			// output file names.
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

				return remoteWorkflowRepository.save(phylogenomicsWorkflow);
			}
		} catch (Exception e) {
			logger.error("Could not connect to Galaxy. Not attempting to auto-configure workflow.");
		}

		return null;
	}

	@Bean(name = "remoteWorkflow")
	@Profile("it")
	public RemoteWorkflowPhylogenomics testRemoteWorkflow() {
		return new RemoteWorkflowPhylogenomics("1", "1", "1", "1", "1", "1", "1");
	}

	/**
	 * @return A RemoteWorkflowServicePhylogenomics with a correctly implemented
	 *         workflow.
	 * @throws WorkflowException
	 */
	@Lazy
	@Bean
	public RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomics(
			final RemoteWorkflowRepository remoteWorkflowRepository, final WorkflowsClient workflowClient,
			final GalaxyWorkflowService galaxyWorkflowService) throws WorkflowException {
		return new RemoteWorkflowServicePhylogenomics(remoteWorkflow(remoteWorkflowRepository, workflowClient,
				galaxyWorkflowService));
	}
}
