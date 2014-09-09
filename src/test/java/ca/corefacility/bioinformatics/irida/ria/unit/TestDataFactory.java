package ca.corefacility.bioinformatics.irida.ria.unit;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
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
	public static final String FAKE_FILE_PATH = "src/test/resources/files/test_file{id}.fastq";
	public static final String FAKE_EXECUTION_MANAGER_ID = "Whole Genome Phyogenomics Pipeline";

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
		Path path = Paths.get("/tmp/sequence-files/fake-file" + Math.random() + ".fast");
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

	private static Analysis constructAnalysis() {
		Set<SequenceFile> files = ImmutableSet.of(
				constructSequenceFile()
		);
		AnalysisPhylogenomicsPipeline analysis = new AnalysisPhylogenomicsPipeline(files, FAKE_EXECUTION_MANAGER_ID);
		analysis.setPhylogeneticTree(constructAnalysisOutputFile(""));
		analysis.setSnpMatrix(constructAnalysisOutputFile("_1"));
		analysis.setSnpTable(constructAnalysisOutputFile("_2"));
		return analysis;
	}

	private static AnalysisOutputFile constructAnalysisOutputFile(String id) {
		return new AnalysisOutputFile(Paths.get(FAKE_FILE_PATH.replace("{id}", id)), FAKE_EXECUTION_MANAGER_ID);
	}
}
