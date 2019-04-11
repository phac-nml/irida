package ca.corefacility.bioinformatics.irida.ria.unit;

import static org.junit.Assert.fail;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.exceptions.AnalysisAlreadySetException;
import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssemblyFromAnalysis;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ToolExecution;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowInput;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowOutput;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowToolRepository;
import ca.corefacility.bioinformatics.irida.model.workflow.structure.IridaWorkflowStructure;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

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
	
	public static SingleEndSequenceFile constructSingleEndSequenceFile(){
		Path path = Paths.get("/tmp/sequence-files/fake-file1.fast");
		return new SingleEndSequenceFile(new SequenceFile(path));
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
	
	public static GenomeAssembly constructGenomeAssembly() {
		AnalysisSubmission submission = constructAnalysisSubmission();
		return new GenomeAssemblyFromAnalysis(submission);
	}
	
	public static AnalysisSubmission constructAnalysisSubmission() {
		return constructAnalysisSubmission(UUID.randomUUID());
	}

	public static AnalysisSubmission constructAnalysisSubmission(UUID workflowId) {
		Set<SequencingObject> files = new HashSet<>();
		files.add(constructSingleEndSequenceFile());
		Long id = 5L;
		final ReferenceFile rf = new ReferenceFile(files.iterator().next().getFiles().iterator().next().getFile());
		rf.setId(id);
		AnalysisSubmission analysisSubmission = AnalysisSubmission.builder(workflowId)
				.name("submission-" + id)
				.inputFiles(files)
				.referenceFile(rf)
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
				.put("tree", constructAnalysisOutputFile("snp_tree.tree", null))
				.put("matrix", constructAnalysisOutputFile("test_file_1.fastq", null))
				.put("table", constructAnalysisOutputFile("test_file_2.fastq", null))
				.put("contigs-with-repeats", constructAnalysisOutputFile("test_file.fasta", null))
				.put("refseq-masher-matches", constructAnalysisOutputFile("refseq-masher-matches.tsv", 9000L))
				.build();
		Analysis analysis = new Analysis(FAKE_EXECUTION_MANAGER_ID, analysisOutputFiles, BuiltInAnalysisTypes.PHYLOGENOMICS);
		return analysis;
	}

	public static User constructUser() {
		User user = new User(USER_ID, "test", "test@me.com", "pass1234", "mr", "test", "123-4567");
		return user;
	}

	public static List<SampleSequencingObjectJoin> generateSequencingObjectsForSample(Sample sample) {
		List<SampleSequencingObjectJoin> join = new ArrayList<>();
		for (long i = 0; i < 5; i++) {
			Path path = Paths.get("/tmp/sequence-files/fake-file" + Math.random() + ".fast");
			SequenceFile file = new SequenceFile(path);
			file.setId(i);
			SingleEndSequenceFile obj = new SingleEndSequenceFile(file);
			obj.setId(i);
			join.add(new SampleSequencingObjectJoin(sample, obj));
		}
		return join;
	}

	private static AnalysisOutputFile constructAnalysisOutputFile(String name, Long id) {
		if (id == null) {
			id = 1L;
		}
		ToolExecution toolExecution = new ToolExecution(1L, null, "testTool", "0.0.12", "executionManagersId",
				ImmutableMap.of());
		final AnalysisOutputFile of = new AnalysisOutputFile(Paths.get(FAKE_FILE_PATH.replace("{name}", name)), "", FAKE_EXECUTION_MANAGER_ID,
				toolExecution);
		final DirectFieldAccessor dfa = new DirectFieldAccessor(of);
		dfa.setPropertyValue("id", id);
		return of;
	}

	public static Project constructProject() {
		Project project = new Project(PROJECT_NAME);
		project.setId(PROJECT_ID);
		project.setOrganism(PROJECT_ORGANISM);
		project.setModifiedDate(new Date(PROJECT_MODIFIED_DATE));
		return project;
	}

	public static IridaWorkflow getIridaWorkflow(UUID id) {
		IridaWorkflowInput input = new IridaWorkflowInput();
		List<IridaWorkflowOutput> outputs = ImmutableList.of(new IridaWorkflowOutput());
		List<IridaWorkflowToolRepository> tools = ImmutableList.of();
		List<IridaWorkflowParameter> parameters = ImmutableList.of();
		IridaWorkflowDescription description = new IridaWorkflowDescription(id, "My Workflow", "V1",
				BuiltInAnalysisTypes.DEFAULT, input, outputs, tools, parameters);
		IridaWorkflowStructure structure = new IridaWorkflowStructure(null);
		return new IridaWorkflow(description, structure);
	}

	public static Map<Project, List<Sample>> constructCart() {
		Project project = constructProject();
		List<Sample> samples = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			samples.add(constructSample());
		}
		return ImmutableMap.of(
				project, samples
		);
	}

	public static List<Project> constructListJoinProjectUser(User user) {
		List<Project> list = new ArrayList<>();
		Project project = constructProject();
		for (int i = 0; i < 10; i++) {
			list.add(project);
		}
		return list;
	}

	public static List<Join<Project, Sample>> constructListJoinProjectSample() {
		List<Join<Project, Sample>> list = new ArrayList<>();
		Sample sample = constructSample();
		Project project = constructProject();
		for (int i = 0; i < 10; i++) {
			list.add(new ProjectSampleJoin(project, sample, true));
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

	public static Page<ProjectSampleJoin> getPageOfProjectSampleJoin() {
		return new Page<ProjectSampleJoin>() {
			@Override public int getTotalPages() {
				return 1;
			}

			@Override public long getTotalElements() {
				return 1;
			}

			@Override public int getNumber() {
				return 1;
			}

			@Override public int getSize() {
				return 1;
			}

			@Override public int getNumberOfElements() {
				return 1;
			}

			@Override public List<ProjectSampleJoin> getContent() {
				Project project = new Project("Joined Project");
				project.setId(1L);
				Sample sample = new Sample("Joined Sample");
				sample.setId(23L);
				ProjectSampleJoin join = new ProjectSampleJoin(project, sample, true);
				return ImmutableList.of(
					join
				);
			}

			@Override public boolean hasContent() {
				return false;
			}

			@Override public Sort getSort() {
				return null;
			}

			@Override public boolean isFirst() {
				return false;
			}

			@Override public boolean isLast() {
				return false;
			}

			@Override public boolean hasNext() {
				return false;
			}

			@Override public boolean hasPrevious() {
				return false;
			}

			@Override public Pageable nextPageable() {
				return null;
			}

			@Override public Pageable previousPageable() {
				return null;
			}

			@Override public Iterator<ProjectSampleJoin> iterator() {
				return null;
			}
		};
	}
}
