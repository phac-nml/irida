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
import java.io.FileNotFoundException;

/**
 * {@link AnalysisSampleUpdater} that adds a number of results from a PHANTASTIC run to the metadata of a {@link Sample}
 */
@Component
public class PHANTASTICSampleUpdater implements AnalysisSampleUpdater {
    private static final Logger logger = LoggerFactory.getLogger(PHANTASTICSampleUpdater.class);
	private static final String PHANTASTIC_FILE = "phantastic_type";
	private static final String PHANTASTIC_DM = "phantastic_dm";

	private MetadataTemplateService metadataTemplateService;
	private IridaWorkflowsService iridaWorkflowsService;
	private SampleService sampleService;

    //ISS
    private EmailController emailController;
    private UserRepository userRepository;
	private final ProjectSampleJoinRepository psjRepository;
	private final ProjectUserJoinRepository pujRepository;
	private final ProjectService projectService;


	// @formatter:off
	private static Map<String, String> PHANTASTIC_LM_FIELDS = ImmutableMap.<String,String>builder()
		.put("information_name", "Sample_code")
		.put("qc_status", "QC_status")
		.put("region", "Regione")
		.put("year", "Anno")
		.put("mlst_ST", "MLST_ST")
		.put("mlst_CC", "MLST_CC")
		.put("mlst_lineage", "MLST_Lineage")
		.put("serotype_serogroup", "Serogroup")
		.put("serotype_amplicons", "Amplicons")
		.put("sample_genes_mapped", "cgMLST_genes_mapped")
		.build();
	// @formatter:on
	// @formatter:off
	private static Map<String, String> PHANTASTIC_EC_FIELDS = ImmutableMap.<String,String>builder()
		.put("information_name", "Sample_code")
		.put("qc_status", "QC_status")
		.put("region", "Regione")
		.put("year", "Anno")
		.put("mlst_ST", "MLST_ST")
		.put("serotype_o", "Antigen_O")
		.put("serotype_h", "Antigen_H")
		.put("virulotype_eae", "eae")
		.put("virulotype_ehxa", "ehxa")
		.put("virulotype_stx1", "stx1")
		.put("virulotype_stx2", "stx2")
		.put("virulotypes_all", "Virulotipi")
		.put("shigatoxin_subtype", "stx_subtype")
		.put("sample_genes_mapped", "cgMLST_genes_mapped")
		.build();
	// @formatter:on

	@Autowired
	public PHANTASTICSampleUpdater(MetadataTemplateService metadataTemplateService, SampleService sampleService,
							  IridaWorkflowsService iridaWorkflowsService, EmailController emailController,
                              ProjectSampleJoinRepository psjRepository, ProjectUserJoinRepository pujRepository,
							  ProjectService projectService) {
		this.metadataTemplateService = metadataTemplateService;
		this.sampleService = sampleService;
		this.iridaWorkflowsService = iridaWorkflowsService;

		this.psjRepository = psjRepository;
		this.pujRepository = pujRepository;
		this.projectService = projectService;
        this.emailController = emailController;
	}

	/**
	 * Add PHANTASTIC results to the metadata of the given {@link Sample}s
	 *
	 * @param samples  The samples to update.
	 * @param analysis the {@link AnalysisSubmission} to apply to the samples
	 * @throws PostProcessingException if the method cannot read the "phantastic_out" output file
	 */
	@Override
	public void update(Collection<Sample> samples, AnalysisSubmission analysis) throws PostProcessingException {
		AnalysisOutputFile phantasticFile = analysis.getAnalysis().getAnalysisOutputFile(PHANTASTIC_FILE);
		String analysisName = analysis.getName();
        ArrayList<String> sampleSpecies = new ArrayList<String>();

		Path filePath = phantasticFile.getFile();

        List<String> recipients = new ArrayList<String>();
		ArrayList<String> clusters = new ArrayList<String>();
		ArrayList<String> sampleCodes = new ArrayList<String>();
		String clusterId = "-";
		String metaClusterId;
		Integer clusterCriterium = 10;

		Map<String, MetadataEntry> stringEntries = new HashMap<>();
		try {
			IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(analysis.getWorkflowId());
			String workflowVersion = iridaWorkflow.getWorkflowDescription().getVersion();

			//Read the JSON file from PHANTASTIC output
			@SuppressWarnings("resource")
			String jsonFile = new Scanner(new BufferedReader(new FileReader(filePath.toFile()))).useDelimiter("\\Z")
					.next();

			// map the results into a Map
			ObjectMapper mapper = new ObjectMapper();
			List<Map<String, Object>> phantasticResults = mapper
					.readValue(jsonFile, new TypeReference<List<Map<String, Object>>>() {
					});

			if (phantasticResults.size() > 0) {
				Map<String, Object> result = phantasticResults.get(0);

				Integer clusterExtendedCriterium = 15;
				if (result.containsKey("relative_reduced_schema_size_%") && result.get("relative_reduced_schema_size_%") != null) {
					Float fltReduction = Float.parseFloat(result.get("relative_reduced_schema_size_%").toString());
					logger.debug("fltReduction: " + fltReduction.toString());
					if (Float.compare(fltReduction, 97.0f) < 0) { clusterExtendedCriterium = 40; }
				}
				logger.debug("clusterExtendedCriterium: " + clusterExtendedCriterium.toString());
				//loop through each of the requested fields and save the entries
                Map<String, String> PHANTASTIC_FIELDS = PHANTASTIC_EC_FIELDS;
				clusterCriterium = 10;
				Long masterProjectId = 3L;
                if (result.containsKey("serotype_serogroup")) {
                    PHANTASTIC_FIELDS = PHANTASTIC_LM_FIELDS;
					clusterCriterium = 7;
					masterProjectId = 4L;
					PipelineProvidedMetadataEntry metadataEntry = new PipelineProvidedMetadataEntry("-", "text", analysis);
					stringEntries.put("Serotype", metadataEntry);
                }
				PHANTASTIC_FIELDS.entrySet().forEach(e -> {
					if (result.containsKey(e.getKey()) && result.get(e.getKey()) != null) {
						String value = result.get(e.getKey()).toString();
						PipelineProvidedMetadataEntry metadataEntry = new PipelineProvidedMetadataEntry(value, "text", analysis);

                        //sample_code
                        if (e.getValue().equals("Sample_code")) { 
							stringEntries.put(e.getValue(), metadataEntry);
							sampleCodes.add(value);
						}
                        else {
							stringEntries.put(e.getValue(), metadataEntry);
                        }
					}
				});
				Float fltRelMapped = 0.0f;
				if (result.containsKey("sample_genes_mapped") && result.get("sample_genes_mapped") != null) {
					Float fltMapped = Float.parseFloat(result.get("sample_genes_mapped").toString());
					logger.debug("fltMapped: " + fltMapped.toString());
					if (result.containsKey("core_genome_schema_size") && result.get("core_genome_schema_size") != null) {
						Float fltLoci = Float.parseFloat(result.get("core_genome_schema_size").toString());
						logger.debug("fltLoci: " + fltLoci.toString());
						fltRelMapped = fltMapped / fltLoci;
					}
				}
				logger.debug("fltRelMapped: " + fltRelMapped.toString());
				if (Float.compare(fltRelMapped, 0.8f) < 0) {
					//less than 80% of loci have been found, no cluster analysis
					clusters.add("RERUN");
					clusters.add("-");
					clusters.add("-");
					clusters.add("-");
					clusters.add("-");
					clusters.add("-");
					clusterId = "-";
					metaClusterId = "-";
				} else {
					//no cluster: clusters = (clusterId, sample1, dist1, sample2, dist2, sample3, dist3)
					clusters = getCluster(sampleCodes.get(0), clusterCriterium, clusterExtendedCriterium, masterProjectId, analysis);
					clusterId = clusters.get(0);
					clusters.remove(0);
					//clusterSampleCodes for sampleService.getRecipientsByCodes to avoid "local variables referenced from a lambda expression must be final or effectively final" error
					Boolean isAlert = (!clusterId.equals("-") && !clusterId.contains("_ext"));
					if (isAlert) { sampleCodes.addAll(clusters); }
					metaClusterId = clusterId;
					if (clusterId.equals("-_ext")) { metaClusterId = "-"; }
				}
				PipelineProvidedMetadataEntry metadataEntry = new PipelineProvidedMetadataEntry(metaClusterId, "text", analysis);
				stringEntries.put("Cluster_Id", metadataEntry);

				//ISS: Add cluster information to the PHANTASTIC_FILE json file
				try {
					// input the file content to the StringBuffer "input"
					BufferedReader jsonreader = new BufferedReader(new FileReader(filePath.toFile()));
					String jsoninputStr = jsonreader.readLine();
					jsonreader.close();
					jsoninputStr = jsoninputStr.replace("}]", ", \"Cluster_Id\": \"" + metaClusterId + "\"}]"); 
					BufferedWriter jsonwriter = new BufferedWriter(new FileWriter(filePath.toFile()));
                    jsonwriter.write(jsoninputStr);
					jsonwriter.close();
					//FileOutputStream jsonfileOut = new FileOutputStream(filePath.toFile());
					//jsonfileOut.write(jsoninputStr.getBytes());
					//jsonfileOut.close();

				} catch (Exception e) {
					System.out.println("Problem reading PHANTASTIC_FILE json file.");
				}
				//ISS: Add cluster information to the PHANTASTIC_FILE json file
				
				// convert string map into metadata fields
				Set<MetadataEntry> metadataSet = metadataTemplateService.convertMetadataStringsToSet(stringEntries);

				//save metadata back to sample
				samples.forEach(s -> {
					sampleService.mergeSampleMetadata(s, metadataSet);
				});

				//EMAIL : if cluster send e-mail to all members of projects of coinvolved samples, if not only to members of this sample's project
				// if isAlert send e-mail also to ROLE_MANAGER members
				// if project.isInternalProject only to members of this sample's project
				Boolean isAlert = (!clusterId.equals("-") && !clusterId.contains("_ext"));
				samples.forEach(s -> {
					logger.debug("isAlert: " + isAlert.toString());
					sampleSpecies.add(s.getOrganism());
					List<Join<Project, Sample>> projectsForSample = psjRepository.getProjectForSample(s);					
					for (Join<Project, Sample> projectForSample : projectsForSample) {
						Project project = projectForSample.getSubject();
						if (!project.isMasterProject()) {
							if (project.isInternalProject()) {
								logger.debug("isInternalProject - sampleCodes: " + String.join(",", sampleCodes.subList(0, 1)));
							} else {
								logger.debug("Not isInternalProject - sampleCodes: " + String.join(",", sampleCodes));
								recipients.addAll(sampleService.getRecipientsByCodes(project, sampleCodes, isAlert));
							}
							List<Join<Project, User>> projectUsers = pujRepository.getUsersForProject(projectForSample.getSubject());
							for (Join<Project, User> projectUser : projectUsers) {
								if (isAlert) {
									if (!recipients.contains(projectUser.getObject().getEmail()))
										{ recipients.add(projectUser.getObject().getEmail());}										
								} else {										
									if (!recipients.contains(projectUser.getObject().getEmail()) && !projectUser.getObject().getSystemRole().equals(Role.ROLE_MANAGER))
										{ recipients.add(projectUser.getObject().getEmail());}
								}
							}
						}
					}
				});
			} else {
				throw new PostProcessingException("PHANTASTIC results for file are not correctly formatted");
			}

		} catch (IOException e) {
			throw new PostProcessingException("Error parsing JSON from PHANTASTIC results", e);
		} catch (IridaWorkflowNotFoundException e) {
			throw new PostProcessingException("Workflow is not found", e);
		}
		if (emailController.isMailConfigured()) {
		   	emailController.sendEndOfAnalysisEmail(String.join(",", recipients), analysisName, sampleCodes.get(0), sampleSpecies.get(0), clusterId, Long.toString(clusterCriterium), String.join(", ", clusters));
        }
	}

	private ArrayList<String> getCluster(String sampleCode, Integer clusterCriterium, Integer clusterExtendedCriterium, Long masterProjectId, AnalysisSubmission analysis ) throws FileNotFoundException {
		//function that checks the distance matrix and searches for clusters
		//an ArrayList clusters is returned with the clusterId in the first position and the cluster nodes in all the following
		//if no cluster is found clusterId equals "-" and the next six positions in the ArrayList contain the names and the distances of the nearest three samples
		AnalysisOutputFile phantasticDM = analysis.getAnalysis().getAnalysisOutputFile(PHANTASTIC_DM);
		Path dmPath = phantasticDM.getFile();
		ArrayList<String> clusterNodes = new ArrayList<String>();
		ArrayList<String> clusterExtendedNodes = new ArrayList<String>();
		ArrayList<String> clusters = new ArrayList<String>();
		Project masterProject = projectService.read(masterProjectId);
		//check for clusters
		Scanner dm_input = new Scanner(new BufferedReader(new FileReader(dmPath.toFile())));
		String firstLine = dm_input.nextLine();
		Integer i = 0;
		List<String> dm_header = new ArrayList<>(Arrays.asList(firstLine.split("\t")));

		Integer dist1 = 9999;
		Integer dist2 = 9999;
		Integer dist3 = 9999;
		String sample1 = "ERROR";
		String sample2 = "-";
		String sample3 = "-";
		while(dm_input.hasNextLine()) {
			Scanner dm_colReader = new Scanner(dm_input.nextLine());
			String firstCol = dm_colReader.next();
			//search for the line of the current sample
			if (sampleCode.equals(firstCol)) {
				while(dm_colReader.hasNextInt()) {
					i++;
					int colNextInt = dm_colReader.nextInt();
					//store the names and distances of the three nearest samples
					if (colNextInt < dist1 && !sampleCode.equals(dm_header.get(i))) {
						sample3 = sample2;
						sample2 = sample1;
						sample1 = dm_header.get(i);
						dist3 = dist2;
						dist2 = dist1;
						dist1 = colNextInt;
					} else {
						if (colNextInt <= dist2 && !sampleCode.equals(dm_header.get(i))) {
							sample3 = sample2;
							sample2 = dm_header.get(i);
							dist3 = dist2;
							dist2 = colNextInt;
						} else {
							if (colNextInt < dist3 && !sampleCode.equals(dm_header.get(i))) {
								sample3 = dm_header.get(i);
								dist3 = colNextInt;
							}
						}					
					}
					//apply the criterium for a cluster
					if (colNextInt <= clusterCriterium && !sampleCode.equals(dm_header.get(i))) { clusterNodes.add(dm_header.get(i)); }
					else { if (colNextInt <= clusterExtendedCriterium && !sampleCode.equals(dm_header.get(i))) { clusterExtendedNodes.add(dm_header.get(i)); } }
				}
				if (sample1.equals("ERROR")) { sample1 = "-"; } //no error, first sample of serotype
			}
		}

		Integer clusterNodesSize = clusterNodes.size();
		Integer clusterExtendedNodesSize = clusterExtendedNodes.size();
		logger.debug("clusterNodes.size: " + clusterNodesSize.toString());
		logger.debug("clusterExtendedNodes.size: " + clusterExtendedNodesSize.toString());
		if (clusterNodes.size() == 0) {
			if (clusterExtendedNodes.size() == 0) {
				//no cluster
				logger.debug("no cluster");
				clusters.add("-");
				clusters.add(sample1);
				clusters.add(dist1.toString());
				clusters.add(sample2);
				clusters.add(dist2.toString());
				clusters.add(sample3);
				clusters.add(dist3.toString());
			} else {
				//sample is extended of an existing cluster (or extended of extended => no cluster)
				String clusterId = sampleService.getClusterIdByCodes(masterProject, clusterExtendedNodes);
				logger.debug("sample is extended of an existing cluster: " + clusterId);
				if (clusterId.contains("_ext")) { clusterId = "-_ext"; } else { clusterId = clusterId + "_ext"; }
				clusters.add(clusterId);
				clusters.add(sample1);
				clusters.add(dist1.toString());
				clusters.add(sample2);
				clusters.add(dist2.toString());
				clusters.add(sample3);
				clusters.add(dist3.toString());
				clusters.add(clusterExtendedCriterium.toString());
			}
		} else {
			logger.debug("getClusterIdByCodes");
			String clusterId = sampleService.getClusterIdByCodes(masterProject, clusterNodes);
			logger.debug("clusterId: " + clusterId);
			if (clusterId.equals("-")) {
				//new cluster
				String newClusterId = sampleService.getNextClusterId(masterProject);
				logger.debug("new cluster: " + newClusterId);
				clusters.add(newClusterId);
				sampleService.setClusterIdByCode(masterProject, clusterNodes, newClusterId);
			} else {
				if (clusterId.contains("_ext")) {
					//other samples are at most extended of an existing cluster => new cluster
					String newClusterId = sampleService.getNextClusterId(masterProject);
					logger.debug("other samples are at most extended of an existing cluster => new cluster: " + newClusterId);
					clusters.add(newClusterId);
				} else {
					//other samples are part of an existing cluster
					logger.debug("other samples are part of an existing cluster: " + clusterId);
					clusters.add(clusterId);
					sampleService.setClusterIdByCode(masterProject, clusterNodes, clusterId);
				}
			}
		}
		clusters.addAll(clusterNodes);
		return clusters;
	}

	@Override
	public AnalysisType getAnalysisType() {
		return BuiltInAnalysisTypes.PHANTASTIC_TYPING;
	}
}
