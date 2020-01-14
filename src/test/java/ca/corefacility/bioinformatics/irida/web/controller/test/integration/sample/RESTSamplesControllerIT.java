package ca.corefacility.bioinformatics.irida.web.controller.test.integration.sample;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils;
import ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestSystemProperties;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Lists;
import com.google.common.net.HttpHeaders;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.*;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Integration tests for project samples.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/web/controller/test/integration/sample/RESTSamplesControllerIntegrationTest.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class RESTSamplesControllerIT {
	private static final Logger logger = LoggerFactory.getLogger(RESTSamplesControllerIT.class);

	public static final String SAMPLE_BASE = "/api/samples/1";

	@Test
	public void testPatchSampleCollectionDateAsUser() {
		Map patchData = new HashMap();
		patchData.put("collectionDate", "2000-01-01");
		asUser().patch(ITestSystemProperties.BASE_URL + SAMPLE_BASE, patchData).then()
				.body("resource.collectionDate", equalTo("2000-01-01"));
	}

}
