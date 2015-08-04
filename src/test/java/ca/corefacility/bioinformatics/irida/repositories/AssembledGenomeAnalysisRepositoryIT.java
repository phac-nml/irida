package ca.corefacility.bioinformatics.irida.repositories;

import static org.junit.Assert.*;

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

import ca.corefacility.bioinformatics.irida.config.IridaApiNoGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.model.genomeFile.AssembledGenomeAnalysis;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFilePairRepository;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * Tests out the {@link AssembledGenomeAnalysisRepository}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiNoGalaxyTestConfig.class, IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/repositories/AssembledGenomeAnalysisRepositoryIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AssembledGenomeAnalysisRepositoryIT {

	@Autowired
	private SequenceFilePairRepository sequenceFilePairRepository;

	@Autowired
	private AssembledGenomeAnalysisRepository assembledGenomeAnalysisRepository;

	@Test
	public void testGetAssembledGenomeAnalysisOne() {
		SequenceFilePair s = sequenceFilePairRepository.findOne(1L);
		AssembledGenomeAnalysis a = assembledGenomeAnalysisRepository.getAssembledGenomeForSequenceFilePair(s);
		assertNotNull("There should be an assembly for this sequence file pair", a);
		assertEquals("Assembled genome should have correct id", new Long(1L), a.getId());
	}

	@Test
	public void testGetAssembledGenomeAnalysisNone() {
		SequenceFilePair s = sequenceFilePairRepository.findOne(2L);
		assertNull("Should be no assembly for this sequence file pair",
				assembledGenomeAnalysisRepository.getAssembledGenomeForSequenceFilePair(s));
	}

	@Test
	public void testGetAssembledGenomeAnalysisAnotherOne() {
		SequenceFilePair s = sequenceFilePairRepository.findOne(3L);
		AssembledGenomeAnalysis a = assembledGenomeAnalysisRepository.getAssembledGenomeForSequenceFilePair(s);
		assertNotNull("There should be an assembly for this sequence file pair", a);
		assertEquals("Assembled genome should have correct id", new Long(2L), a.getId());
	}
}
