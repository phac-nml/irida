package ca.corefacility.bioinformatics.irida.ria.unit;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

import com.google.common.collect.ImmutableSet;

/**
 * Generates test data for unit tests.
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
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
	 * Construct a {@link ca.corefacility.bioinformatics.irida.model.SequenceFile}
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
		AnalysisSubmission analysisSubmission = new AnalysisSubmission("submission-" + id, files);
		analysisSubmission.setId(id);
		analysisSubmission.setAnalysisState(AnalysisState.COMPLETED);
		analysisSubmission.setAnalysis(constructAnalysis());
		return analysisSubmission;
	}

	public static Analysis constructAnalysis() {
		Set<SequenceFile> files = ImmutableSet.of(
				constructSequenceFile()
		);
		AnalysisPhylogenomicsPipeline analysis = new AnalysisPhylogenomicsPipeline(files, FAKE_EXECUTION_MANAGER_ID);
		analysis.setPhylogeneticTree(constructAnalysisOutputFile("snp_tree.tree"));
		analysis.setSnpMatrix(constructAnalysisOutputFile("test_file_1.fastq"));
		analysis.setSnpTable(constructAnalysisOutputFile("test_file_2.fastq"));
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
		return new AnalysisOutputFile(Paths.get(FAKE_FILE_PATH.replace("{name}", name)), FAKE_EXECUTION_MANAGER_ID);
	}

	public static Project constructProject() {
		Project project = new Project(PROJECT_NAME);
		project.setId(PROJECT_ID);
		project.setOrganism(PROJECT_ORGANISM);
		project.setModifiedDate(new Date(PROJECT_MODIFIED_DATE));
		return project;
	}
}
