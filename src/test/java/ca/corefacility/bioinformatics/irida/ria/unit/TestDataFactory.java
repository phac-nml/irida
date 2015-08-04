package ca.corefacility.bioinformatics.irida.ria.unit;

import static org.junit.Assert.fail;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import ca.corefacility.bioinformatics.irida.exceptions.AnalysisAlreadySetException;
import ca.corefacility.bioinformatics.irida.exceptions.NoSuchValueException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.upload.UploadFolderName;
import ca.corefacility.bioinformatics.irida.model.upload.UploadResult;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ToolExecution;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowInput;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowOutput;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowToolRepository;
import ca.corefacility.bioinformatics.irida.model.workflow.structure.IridaWorkflowStructure;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.UploadWorker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Generates test data for unit tests.
 *
 */
public class TestDataFactory {
	public static final String FAKE_FILE_PATH = "src/test/resources/files/{name}";
	public static final String FAKE_EXECUTION_MANAGER_ID = "Whole Genome Phyogenomics Pipeline";
	public static final long USER_ID = 1L;
	public static final String PROJECT_NAME = "test_project";
	public static final String PROJECT_ORGANISM = "E. coli";
	public static final Long PROJECT_ID = 1L;
	private static final Long PROJECT_MODIFIED_DATE = 1403723706L;

	/**
	 * Construct a simple {@link ca.corefacility.bioinformatics.irida.model.sample.Sample}.
	 *
	 * @return a sample with a name and identifier.
	 */
	public static Sample constructSample() {
		String sampleName = "sampleName";
		Sample s = new Sample();
		s.setSampleName(sampleName);
		s.setId(1L);
		return s;
	}

	/**
	 * Construct a {@link ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile}
	 *
	 * @return A fake sequence files with a randomly generated path.
	 */
	public static SequenceFile constructSequenceFile() {
		Path path = Paths.get("/tmp/sequence-files/fake-file1.fast");
		return new SequenceFile(path);
	}

	/**
	 * Construct a {@link ca.corefacility.bioinformatics.irida.model.project.ReferenceFile}
	 *
	 * @return A fake reference files with a randomly generated path.
	 */
	public static ReferenceFile constructReferenceFile() {
		Path path = Paths.get("/tmp/sequence-files/fake-file" + Math.random() + ".fast");
		return new ReferenceFile(path);
	}

	public static AnalysisSubmission constructAnalysisSubmission() {
		Set<SequenceFile> files = new HashSet<>();
		files.add(constructSequenceFile());
		Long id = 5L;
		AnalysisSubmission analysisSubmission = AnalysisSubmission.builder(UUID.randomUUID())
				.name("submission-" + id)
				.inputFilesSingle(files)
				.build();
		analysisSubmission.setId(id);
		analysisSubmission.setAnalysisState(AnalysisState.COMPLETED);
		try {
			analysisSubmission.setAnalysis(constructAnalysis());
		} catch (final AnalysisAlreadySetException e) {
			// this should *never* happen, we just constructed
			// AnalysisSubmission above.
			fail();
		}
		return analysisSubmission;
	}

	public static Analysis constructAnalysis() {
		Map<String, AnalysisOutputFile> analysisOutputFiles = new ImmutableMap.Builder<String, AnalysisOutputFile>()
				.put("tree", constructAnalysisOutputFile("snp_tree.tree"))
				.put("matrix", constructAnalysisOutputFile("test_file_1.fastq"))
				.put("table", constructAnalysisOutputFile("test_file_2.fastq")).build();
		AnalysisPhylogenomicsPipeline analysis = new AnalysisPhylogenomicsPipeline(FAKE_EXECUTION_MANAGER_ID,
				analysisOutputFiles);
		return analysis;
	}

	public static User constructUser() {
		User user = new User(USER_ID, "test", "test@me.com", "pass1234", "mr", "test", "123-4567");
		return user;
	}

	public static List<Join<Sample, SequenceFile>> generateSequenceFilesForSample(Sample sample) {
		List<Join<Sample, SequenceFile>> join = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Path path = Paths.get("/tmp/sequence-files/fake-file" + Math.random() + ".fast");
			SequenceFile file = new SequenceFile(path);
			file.setId((long) i);
			join.add(new SampleSequenceFileJoin(sample, file));
		}
		return join;
	}

	private static AnalysisOutputFile constructAnalysisOutputFile(String name) {
		ToolExecution toolExecution = new ToolExecution(1L, null, "testTool", "0.0.12", "executionManagersId",
				ImmutableMap.of());
		return new AnalysisOutputFile(Paths.get(FAKE_FILE_PATH.replace("{name}", name)), FAKE_EXECUTION_MANAGER_ID,
				toolExecution);
	}

	public static Project constructProject() {
		Project project = new Project(PROJECT_NAME);
		project.setId(PROJECT_ID);
		project.setOrganism(PROJECT_ORGANISM);
		project.setModifiedDate(new Date(PROJECT_MODIFIED_DATE));
		return project;
	}

	public static UploadWorker constructUploadWorker() {
		return new UploadWorker() {
			@Override public UploadResult getUploadResult() {
				return null;
			}

			@Override public UploadException getUploadException() {
				return null;
			}

			@Override public float getProportionComplete() {
				return 33.3f;
			}

			@Override public boolean exceptionOccured() {
				return false;
			}

			@Override public boolean isFinished() {
				return false;
			}

			@Override public int getTotalSamples() throws NoSuchValueException {
				return 0;
			}

			@Override public int getCurrentSample() throws NoSuchValueException {
				return 0;
			}

			@Override public UploadFolderName getSampleName() throws NoSuchValueException {
				return null;
			}

			@Override public void run() {

			}

			@Override public void sampleProgressUpdate(int i, int i1, UploadFolderName uploadFolderName) {

			}
		};
	}

	public static IridaWorkflow getIridaWorkflow(UUID id) {
		IridaWorkflowInput input = new IridaWorkflowInput();
		List<IridaWorkflowOutput> outputs = ImmutableList.of(new IridaWorkflowOutput());
		List<IridaWorkflowToolRepository> tools = ImmutableList.of();
		List<IridaWorkflowParameter> parameters = ImmutableList.of();
		IridaWorkflowDescription description = new IridaWorkflowDescription(id, "My Workflow", "V1",
				AnalysisType.DEFAULT, input, outputs, tools, parameters);
		IridaWorkflowStructure structure = new IridaWorkflowStructure(null);
		return new IridaWorkflow(description, structure);
	}

	public static Map<Project, Set<Sample>> constructCart() {
		Project project = constructProject();
		Set<Sample> samples = new HashSet<>();
		for (int i = 0; i < 10; i++) {
			samples.add(constructSample());
		}
		return ImmutableMap.of(
				project, samples
		);
	}

	public static List<Join<Project, User>> constructListJoinProjectUser(User user) {
		List<Join<Project, User>> list = new ArrayList<>();
		Project project = constructProject();
		for (int i = 0; i < 10; i++) {
			list.add(new ProjectUserJoin(project, user, ProjectRole.PROJECT_USER));
		}
		return list;
	}

	public static List<Join<Project, Sample>> constructListJoinProjectSample() {
		List<Join<Project, Sample>> list = new ArrayList<>();
		Sample sample = constructSample();
		Project project = constructProject();
		for (int i = 0; i < 10; i++) {
			list.add(new ProjectSampleJoin(project, sample));
		}
		return list;
	}

	public static List<Project> constructProjectList() {
		List<Project> projects = new ArrayList<>();
		Project project = constructProject();
		for (int i = 0; i < 50; i++) {
			projects.add(project);
		}
		return projects;
	}
}
