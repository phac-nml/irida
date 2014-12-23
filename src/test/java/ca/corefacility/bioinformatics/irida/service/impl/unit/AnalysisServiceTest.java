package ca.corefacility.bioinformatics.irida.service.impl.unit;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisOutputFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.impl.AnalysisServiceImpl;

import com.google.common.collect.Sets;

public class AnalysisServiceTest {

	private AnalysisService analysisService;

	private AnalysisRepository analysisRepository;
	private AnalysisOutputFileRepository analysisOutputFileRepository;
	private Validator validator;

	@Before
	public void setUp() {
		this.analysisRepository = mock(AnalysisRepository.class);
		this.analysisOutputFileRepository = mock(AnalysisOutputFileRepository.class);
		this.validator = mock(Validator.class);

		this.analysisService = new AnalysisServiceImpl(analysisRepository, analysisOutputFileRepository, validator);
	}

	@Test
	public void testCreateAnalysisWithOneOutputFile() throws IOException {
		SequenceFile sf = new SequenceFile();
		AnalysisFastQC analysis = new AnalysisFastQC(Sets.newHashSet(sf), "something");
		Path outputFile = Files.createTempFile(null, null);
		AnalysisOutputFile report = new AnalysisOutputFile(outputFile, "");
		analysis.setFastQCReport(report);

		analysisService.create(analysis);

		verify(analysisOutputFileRepository).save(report);
		verify(analysisRepository).save(analysis);
	}

	@Test
	public void testCreateAnalysisWithMultipleOutputFile() throws IOException {
		SequenceFile sf = new SequenceFile();
		AnalysisPhylogenomicsPipeline analysis = new AnalysisPhylogenomicsPipeline(Sets.newHashSet(sf), "something");
		Path outputFile1 = Files.createTempFile(null, null);
		Path outputFile2 = Files.createTempFile(null, null);
		Path outputFile3 = Files.createTempFile(null, null);
		AnalysisOutputFile report1 = new AnalysisOutputFile(outputFile1, "");
		AnalysisOutputFile report2 = new AnalysisOutputFile(outputFile2, "");
		AnalysisOutputFile report3 = new AnalysisOutputFile(outputFile3, "");
		analysis.setPhylogeneticTree(report1);
		analysis.setSnpMatrix(report2);
		analysis.setSnpTable(report3);

		analysisService.create(analysis);

		verify(analysisOutputFileRepository, times(3)).save(any(AnalysisOutputFile.class));
		verify(analysisRepository).save(analysis);
	}
}
