package ca.corefacility.bioinformatics.irida.service.impl.unit;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisOutputFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.impl.AnalysisServiceImpl;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AnalysisServiceTest {

	private AnalysisService analysisService;

	private AnalysisRepository analysisRepository;
	private AnalysisOutputFileRepository analysisOutputFileRepository;
	private Validator validator;

	@Before
	public void setUp() {
		this.analysisRepository = mock(AnalysisRepository.class);
		this.analysisOutputFileRepository = mock(AnalysisOutputFileRepository.class);		this.validator = mock(Validator.class);

		this.analysisService = new AnalysisServiceImpl(analysisRepository, analysisOutputFileRepository, validator);
	}

	@Test
	public void testCreateAnalysisWithOneOutputFile() throws IOException {
		Path outputFile = Files.createTempFile(null, null);
		AnalysisOutputFile report = new AnalysisOutputFile(outputFile, "", "", null);
		Analysis analysis = new Analysis(null, ImmutableMap.of("file", report), new AnalysisType("test"));

		analysisService.create(analysis);

		verify(analysisOutputFileRepository).save(report);
		verify(analysisRepository).save(analysis);
	}

	@Test
	public void testCreateAnalysisWithMultipleOutputFile() throws IOException {
		Path outputFile1 = Files.createTempFile(null, null);
		Path outputFile2 = Files.createTempFile(null, null);
		Path outputFile3 = Files.createTempFile(null, null);
		AnalysisOutputFile report1 = new AnalysisOutputFile(outputFile1, "", "", null);
		AnalysisOutputFile report2 = new AnalysisOutputFile(outputFile2, "", "", null);
		AnalysisOutputFile report3 = new AnalysisOutputFile(outputFile3, "", "", null);
		Map<String, AnalysisOutputFile> analysisOutputFiles = new ImmutableMap.Builder<String, AnalysisOutputFile>()
				.put("tree", report1).put("matrix", report2).put("table", report3).build();
		Analysis analysis = new Analysis("something", analysisOutputFiles, BuiltInAnalysisTypes.PHYLOGENOMICS);

		analysisService.create(analysis);

		verify(analysisOutputFileRepository, times(3)).save(any(AnalysisOutputFile.class));
		verify(analysisRepository).save(analysis);
	}
}
