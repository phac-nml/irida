package ca.corefacility.bioinformatics.irida.config.workflow;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.analysis.AnalysisExecutionServiceConfig;
import ca.corefacility.bioinformatics.irida.config.manager.ExecutionManagerConfig;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.RemoteWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.repositories.workflow.RemoteWorkflowRepository;

/**
 * Installs a remote workflow within the database.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class InstallRemoteWorkflow {
	
	private static final Logger logger = LoggerFactory
			.getLogger(InstallRemoteWorkflow.class);
	
	private static final String USERNAME_NAME = "username";
	private static final String PASSWORD_NAME = "password";

	private static final String WORKFLOW_ID = "workflowId";
	private static final String SEQUENCE_INPUT_LABEL = "inputSequenceLabel";
	private static final String REFERENCE_INPUT_LABEL = "inputReferenceLabel";
	private static final String TREE_OUTPUT_NAME = "outputTreeName";
	private static final String MATRIX_OUTPUT_NAME = "outputMatrixName";
	private static final String SNP_TABLE_OUTPUT_NAME = "outputSnpTableName";

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption(null, USERNAME_NAME, true, "username to access the database.");
		options.addOption(null, PASSWORD_NAME, true, "password for the user.");
		options.addOption(null, WORKFLOW_ID, true, "id of the workflow in Galaxy.");
		options.addOption(null, SEQUENCE_INPUT_LABEL, true, "the label of the input sequence files.");
		options.addOption(null, REFERENCE_INPUT_LABEL, true, "the label of the input reference file.");
		options.addOption(null, TREE_OUTPUT_NAME, true, "the name of the output tree.");
		options.addOption(null, MATRIX_OUTPUT_NAME, true, "the name of the output matrix.");
		options.addOption(null, SNP_TABLE_OUTPUT_NAME, true, "the name of the output snp table.");
		options.addOption("h", "help", false, "print help statement.");
		
		CommandLineParser parser = new BasicParser();
		try {
			CommandLine commandLine = parser.parse(options, args);
			
			if (commandLine.hasOption("help")) {
				printUsage(options);
			} else {
				handleInstallWorkflow(commandLine, options);
			}
		} catch (ParseException e) {
			logger.error("Error: Command line could not be parsed: " + e.getMessage());
			printUsage(options);
		}
	}
	
	/**
	 * Prints a usage statement for the given options.
	 * @param options  The options used to print a usage statement.
	 */
	private static void printUsage(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(InstallRemoteWorkflow.class.getSimpleName(), options);
	}
	
	/**
	 * Handles installing a workflow within the database.
	 * @param commandLine  The parsed command line options to use.
	 * @param options  The set of options (for printing usage statements).
	 */
	private static void handleInstallWorkflow(CommandLine commandLine, Options options) {
		String username = null;
		
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.getEnvironment().setActiveProfiles("dev");
			context.register(IridaApiServicesConfig.class);
			context.register(AnalysisExecutionServiceConfig.class);
			context.register(ExecutionManagerConfig.class);
			context.refresh();
			
			GalaxyWorkflowService galaxyWorkflowService = context.getBean(GalaxyWorkflowService.class);
			UserRepository userRepository = context.getBean(UserRepository.class);
			RemoteWorkflowRepository workflowRepository = context.getBean(RemoteWorkflowRepository.class);
			AuthenticationProvider authenticationProvider = context.getBean(AuthenticationProvider.class);

			username = getOptionValue(commandLine, USERNAME_NAME);
			Authentication authentication = getAuthentication(commandLine, userRepository, authenticationProvider);
			
			RemoteWorkflowPhylogenomics remoteWorkflow = generateRemoteWorkflow(commandLine, galaxyWorkflowService);
			logger.info("Built remote workflow: " + remoteWorkflow);
			
			logger.info("Attempting to log in as user: " + username);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			if (workflowRepository.exists(remoteWorkflow.getWorkflowId())) {
				logger.info("Workflow " + remoteWorkflow + " already exists, not saving");
			} else {
				logger.info("Saving workflow: " + remoteWorkflow);
				RemoteWorkflow savedWorkflow = workflowRepository.save(remoteWorkflow);
				logger.info("Workflow saved: " + savedWorkflow);
			}
			
			SecurityContextHolder.clearContext();
		} catch (ParseException e) {
			logger.error("Error: Command line could not be parsed: " + e.getMessage());
			printUsage(options);
		} catch (WorkflowException e) {
			logger.error("WorkflowException " + e.getMessage());
		} catch (BadCredentialsException e) {
			logger.error("Invalid credentials for user \"" + username + "\"");
		}
	}

	/**
	 * Gets an authentication object for the given user.
	 * @param commandLine  The commnand line object containing the user information.
	 * @param userRepository  The repository for accessing the user.
	 * @param authenticationProvider  The authentication provider.
	 * @return  An Authentication object for authenticating the given user.
	 * @throws ParseException  If there was an issue parsing user information.
	 */
	private static Authentication getAuthentication(CommandLine commandLine, UserRepository userRepository, AuthenticationProvider authenticationProvider) throws ParseException {
		String userName = getOptionValue(commandLine, USERNAME_NAME);
		String password = getOptionValue(commandLine, PASSWORD_NAME);
		
		User user = userRepository.loadUserByUsername(userName);
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, password);
		
		return authenticationProvider.authenticate(token);
	}

	/**
	 * Builds a new RemoteWorkflowPhylogenomics object.
	 * @param commandLine  The CommandLine object containing the options.
	 * @param workflowService  The service class for interacting with Galaxy.
	 * @return  A RemoteWorkflowPhylogenomics.
	 * @throws ParseException  If there was an issue parsing some of the command line options.
	 * @throws WorkflowException  If there was an issue interacting with Galaxy.
	 */
	private static RemoteWorkflowPhylogenomics generateRemoteWorkflow(
			CommandLine commandLine, GalaxyWorkflowService workflowService) throws ParseException, WorkflowException {
		
		String workflowId = getOptionValue(commandLine, WORKFLOW_ID);
		
		String workflowChecksum = getChecksum(workflowId, workflowService);
		
		String sequenceFileInputLabel = getOptionValue(commandLine,SEQUENCE_INPUT_LABEL);
		String referenceFileInputLabel = getOptionValue(commandLine,REFERENCE_INPUT_LABEL);
		String treeName = getOptionValue(commandLine,TREE_OUTPUT_NAME);
		String matrixName = getOptionValue(commandLine,MATRIX_OUTPUT_NAME);
		String tableName = getOptionValue(commandLine,SNP_TABLE_OUTPUT_NAME);
		
		return new RemoteWorkflowPhylogenomics(workflowId,
			workflowChecksum, sequenceFileInputLabel, referenceFileInputLabel,
			treeName, matrixName, tableName);
	}
	
	/**
	 * Gets the checksum for the given workflow.
	 * @param workflowId  The id of the workflow to find a checksum for.
	 * @param workflowService  The service used to interact with Galaxy workflows.
	 * @return  The checksum of this workflow.
	 * @throws WorkflowException  If there was an issue getting the workflow from Galaxy.
	 */
	private static String getChecksum(String workflowId, GalaxyWorkflowService workflowService) throws WorkflowException {
		return workflowService.getWorkflowChecksum(workflowId);
	}

	/**
	 * Gets a value for the given option, throwing an exception if invalid.
	 * @param commandLine  The commandLine object containing options.
	 * @param name  The name of the option.
	 * @return  The value of the given option.
	 * @throws ParseException  If the value does not exist.
	 */
	private static String getOptionValue(CommandLine commandLine, String name) throws ParseException {
		if (commandLine.hasOption(name)) {
			return commandLine.getOptionValue(name);
		} else {
			throw new ParseException("Missing option for: " + name);
		}
	}
}
