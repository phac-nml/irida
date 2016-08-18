package ca.corefacility.bioinformatics.irida.service.impl.integration.sample;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.test.context.support.WithMockUser;
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
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleMetadata;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleMetadataRepository;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiNoGalaxyTestConfig.class, IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@UsingDataSet(locations = "/ca/corefacility/bioinformatics/irida/service/impl/SampleServiceImplMetadataIT.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/SampleServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class SampleServiceImplMetadataIT {

	/**
	 * Initialize the mongodb connection to insert data
	 */
	@Rule
	public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb("test");

	/**
	 * NoSQLUnit requirement to wire in ApplicationContext. Really not sure why
	 * but it doesn't work if you don't.
	 */
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	SampleMetadataRepository metadataRepo;

	@Autowired
	SampleService sampleService;

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testSampleIdsMatch() {
		Iterable<Sample> findAll2 = sampleService.readMultiple(ImmutableList.of(1L, 2L, 3L));

		for (Sample s : findAll2) {
			SampleMetadata metadataForSample = sampleService.getMetadataForSample(s);
			assertEquals("Sample ids should match", s.getId(), metadataForSample.getSampleId());
		}
	}
}
