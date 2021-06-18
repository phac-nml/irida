package ca.corefacility.bioinformatics.irida.ria.unit.web.projects.metadata;

import java.util.*;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;

import ca.corefacility.bioinformatics.irida.ria.utilities.SampleMetadataStorage;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.ria.web.projects.metadata.ProjectSampleMetadataController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ProjectSampleMetadataControllerTest {

	// Controller
	private ProjectSampleMetadataController controller;

	// Services
	private MessageSource messageSource;
	private ProjectService projectService;
	private SampleService sampleService;
	private MetadataTemplateService metadataTemplateService;
	private ProjectControllerUtils projectControllerUtils;

	@Before
	public void setUp() {
		messageSource = mock(MessageSource.class);
		projectService = mock(ProjectService.class);
		sampleService = mock(SampleService.class);
		metadataTemplateService = mock(MetadataTemplateService.class);
		projectControllerUtils = mock(ProjectControllerUtils.class);

		controller = new ProjectSampleMetadataController(messageSource, projectService, sampleService,
				metadataTemplateService, projectControllerUtils);
	}

	@Test
	public void testSampleMetadataStorage() {
		long projectId = 1L;

		SampleMetadataStorage storage = new SampleMetadataStorage();
		List<String> headers_list = new ArrayList<>();
		headers_list.add("header1");
		headers_list.add("header2");
		headers_list.add("header3");
		storage.saveHeaders(headers_list);
		List<Map<String, String>> rows = new ArrayList<>();
		Map<String, String> rowMap = new HashMap<>();
		rowMap.put("header1", "value1");
		rowMap.put("header2", "value2");
		rowMap.put("header3", "value3");
		rows.add(rowMap);
		storage.saveRows(rows);

		MockMultipartFile file = new MockMultipartFile("file", "test.csv", MediaType.TEXT_PLAIN_VALUE,
				"header1,header2,header3\nvalue1,value2,value3".getBytes());
		HttpSession session = new MockHttpSession();

		controller.createProjectSampleMetadata(session, projectId, file);
		
		assertEquals(storage, session.getAttribute("pm-" + projectId));
	}
}
