package ca.corefacility.bioinformatics.irida.pipeline.results.updater.impl;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.PostProcessingException;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.PipelineProvidedMetadataEntry;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.results.updater.AnalysisSampleUpdater;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

//ISS
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import java.io.FileNotFoundException;

/**
 * {@link AnalysisSampleUpdater} that adds a number of results from a RECOVERY run to the metadata of a {@link Sample}
 */
@Component
public class RECOVERYSampleUpdater implements AnalysisSampleUpdater {
    private static final Logger logger = LoggerFactory.getLogger(RECOVERYSampleUpdater.class);
	private static final String RECOVERY_FILE = "recovery_type";

	private MetadataTemplateService metadataTemplateService;
	private IridaWorkflowsService iridaWorkflowsService;
	private SampleService sampleService;

    //ISS
    private EmailController emailController;
    private UserRepository userRepository;
	private final ProjectSampleJoinRepository psjRepository;
	private final ProjectUserJoinRepository pujRepository;
	private final ProjectService projectService;
	private final UserService userService;

	// @formatter:off
	private static Map<String, String> RECOVERY_FIELDS = ImmutableMap.<String,String>builder()
		.put("information_name", "Sample_code")
		.put("qc_status", "QC_status")
		.put("region", "Regione")
		.put("year", "Anno")
		.put("sequence", "Sequence")
		.put("lineage", "Lineage")
		.put("clade", "Clade")
		.put("ORF1ab", "ORF1ab")
		.put("S-protein", "S-protein")
		.put("ORF3a", "ORF3a")
		.put("E-protein", "E-protein")
		.put("M-protein", "M-protein")
		.put("ORF6", "ORF6")
		.put("ORF7a", "ORF7a")
		.put("ORF7b", "ORF7b")
		.put("ORF8", "ORF8")
		.put("N-protein", "N-protein")
		.put("ORF10", "ORF10")
		.put("N_consensus", "N_consensus")
		.put("variante", "Variante")
		.put("notifica", "Notifica")
		//.put("Intergenic", "Intergenic")
		
		.build();
	// @formatter:on

	@Autowired
	public RECOVERYSampleUpdater(MetadataTemplateService metadataTemplateService, SampleService sampleService,
							  IridaWorkflowsService iridaWorkflowsService, EmailController emailController,
                              ProjectSampleJoinRepository psjRepository, ProjectUserJoinRepository pujRepository,
							  ProjectService projectService, UserService userService) {
		this.metadataTemplateService = metadataTemplateService;
		this.sampleService = sampleService;
		this.iridaWorkflowsService = iridaWorkflowsService;

		this.psjRepository = psjRepository;
		this.pujRepository = pujRepository;
		this.projectService = projectService;
		this.userService = userService;
        this.emailController = emailController;
	}

	/**
	 * Add RECOVERY results to the metadata of the given {@link Sample}s
	 *
	 * @param samples  The samples to update.
	 * @param analysis the {@link AnalysisSubmission} to apply to the samples
	 * @throws PostProcessingException if the method cannot read the "recovery_out" output file
	 */
	@Override
	public void update(Collection<Sample> samples, AnalysisSubmission analysis) throws PostProcessingException {
		AnalysisOutputFile recoveryFile = analysis.getAnalysis().getAnalysisOutputFile(RECOVERY_FILE);
		String analysisName = analysis.getName();

		Path filePath = recoveryFile.getFile();

        List<String> recipients = new ArrayList<String>();
		ArrayList<String> sampleCodes = new ArrayList<String>();
		ArrayList<String> lineages = new ArrayList<String>();
		ArrayList<String> clades = new ArrayList<String>();
		ArrayList<String> sproteins = new ArrayList<String>();
		ArrayList<String> nconsensus = new ArrayList<String>();
		ArrayList<String> variantes = new ArrayList<String>();
		ArrayList<String> notifica = new ArrayList<String>();
		String jsonString;

		Map<String, MetadataEntry> stringEntries = new HashMap<>();
		try {
			IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(analysis.getWorkflowId());
			String workflowVersion = iridaWorkflow.getWorkflowDescription().getVersion();

			//Read the JSON file from RECOVERY output
			@SuppressWarnings("resource")
			String jsonFile = new Scanner(new BufferedReader(new FileReader(filePath.toFile()))).useDelimiter("\\Z").next();
			jsonString = jsonFile;

			// map the results into a Map
			ObjectMapper mapper = new ObjectMapper();
			List<Map<String, Object>> recoveryResults = mapper
					.readValue(jsonFile, new TypeReference<List<Map<String, Object>>>() {
					});
			logger.debug("update: " + jsonString);
			if (recoveryResults.size() > 0) {
				Map<String, Object> result = recoveryResults.get(0);

				//loop through each of the requested fields and save the entries
				Long masterProjectId = 1L;
				RECOVERY_FIELDS.entrySet().forEach(e -> {
					if (result.containsKey(e.getKey()) && result.get(e.getKey()) != null) {
						String value = result.get(e.getKey()).toString();
						PipelineProvidedMetadataEntry metadataEntry = new PipelineProvidedMetadataEntry(value, "text", analysis);

						//sample_code
						if (e.getValue().equals("Sample_code")) { sampleCodes.add(value); }
						if (e.getValue().equals("Lineage")) { lineages.add(value); }
						if (e.getValue().equals("Clade")) { clades.add(value); }
						if (e.getValue().equals("S-protein")) { sproteins.add(value); }
						if (e.getValue().equals("Variante")) { variantes.add(value); }
						if (e.getValue().equals("Notifica")) { notifica.add(value); }
						if (e.getValue().equals("N_consensus")) { nconsensus.add(value); }
						stringEntries.put(e.getValue(), metadataEntry);
					}
				});

				// convert string map into metadata fields
				Set<MetadataEntry> metadataSet = metadataTemplateService.convertMetadataStringsToSet(stringEntries);

				//save metadata back to sample
				samples.forEach(s -> {
					sampleService.mergeSampleMetadata(s, metadataSet);
				});

				//EMAIL : if Notifica send e-mail to all manager members of the project and members of the master project, if not only to submitter
				Boolean isAlert = (!notifica.get(0).equals("-"));
				samples.forEach(s -> {
					logger.debug("isAlert: " + isAlert.toString());
					if (isAlert) {
						List<Join<Project, Sample>> projectsForSample = psjRepository.getProjectForSample(s);
						for (Join<Project, Sample> projectForSample : projectsForSample) {
							Project project = projectForSample.getSubject();
							if (!project.isMasterProject()) {
								String strSequencedBy = (s.getSequencedBy() == null) ? "aries@iss.it" : s.getSequencedBy();
								if (!strSequencedBy.equals("issseq")) { 
									User user = userService.getUserByUsername(strSequencedBy);
									if (!user.getPhoneNumber().equals("0000")) { recipients.add(user.getEmail()); }
								}
								List<Join<Project, User>> projectUsers = pujRepository.getUsersForProject(projectForSample.getSubject());
								for (Join<Project, User> projectUser : projectUsers) {
									if (!recipients.contains(projectUser.getObject().getEmail()) && projectUser.getObject().getSystemRole().equals(Role.ROLE_MANAGER) && (!projectUser.getObject().getPhoneNumber().equals("0000")))
										{ recipients.add(projectUser.getObject().getEmail());}
								}
							}
							else {
								List<Join<Project, User>> projectUsers = pujRepository.getUsersForProject(projectForSample.getSubject());
								for (Join<Project, User> projectUser : projectUsers) {
									if ((!recipients.contains(projectUser.getObject().getEmail())) && (!projectUser.getObject().getPhoneNumber().equals("0000")))
										{ recipients.add(projectUser.getObject().getEmail()); }
								}
							}
						}						
					} else {
						String strSequencedBy = (s.getSequencedBy() == null) ? "aries@iss.it" : s.getSequencedBy();
						if (!strSequencedBy.equals("issseq")) { 
							User user = userService.getUserByUsername(strSequencedBy);
							if (!user.getPhoneNumber().equals("0000")) { recipients.add(user.getEmail()); }
						}
						else { recipients.add("aries@iss.it"); }
					}
				});
			} else {
				throw new PostProcessingException("RECOVERY results for file are not correctly formatted");
			}

		} catch (IOException e) {
			throw new PostProcessingException("Error parsing JSON from RECOVERY results", e);
		} catch (IridaWorkflowNotFoundException e) {
			throw new PostProcessingException("Workflow is not found", e);
		}
		try {
			if (emailController.isMailConfigured()) {
				emailController.sendEndOfAnalysisEmail(String.join(",", recipients), analysisName, sampleCodes.get(0), "Coronavirus", notifica.get(0), jsonString, lineages.get(0)+"/"+clades.get(0)+"/"+variantes.get(0)+"/"+sproteins.get(0)+"/"+nconsensus.get(0));
			}
		} catch (final Exception e) {
			logger.error("End-of-analysis email failed to send", e);
			throw new PostProcessingException("Failed to send e-mail.", e);
		}
	}

	@Override
	public AnalysisType getAnalysisType() {
		return BuiltInAnalysisTypes.RECOVERY_TYPING;
	}
}
