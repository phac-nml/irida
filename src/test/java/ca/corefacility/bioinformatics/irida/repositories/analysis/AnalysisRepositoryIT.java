package ca.corefacility.bioinformatics.irida.repositories.analysis;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * Tests methods in the {@link AnalysisRepository}.
 * 
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiGalaxyTestConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/repositories/analysis/AnalysisRepositoryIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisRepositoryIT {

	@Autowired
	private SequenceFileRepository sequenceFileRepository;

	@Autowired
	private AnalysisRepository analysisRepository;

	private SequenceFile sf1;
	private SequenceFile sf2;

	@Before
	public void setup() {
		sf1 = sequenceFileRepository.findOne(1L);
		sf2 = sequenceFileRepository.findOne(2L);
	}

	@Test
	public void testFindMostRecentAnalysisForSequenceFileSuccess() {
		AnalysisFastQC analysis = analysisRepository.findMostRecentAnalysisForSequenceFile(sf1, AnalysisFastQC.class);
		assertEquals(Long.valueOf(2L), analysis.getId());
	}

	@Test(expected=EntityNotFoundException.class)
	public void testFindMostRecentAnalysisForSequenceFileNoAnalysis() {
		analysisRepository.findMostRecentAnalysisForSequenceFile(sf2, AnalysisFastQC.class);
	}
}
