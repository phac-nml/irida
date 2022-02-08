package ca.corefacility.bioinformatics.irida.web.controller.test.unit.projects.metadata;

import java.util.*;

import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.utilities.SampleMetadataStorage;
import ca.corefacility.bioinformatics.irida.ria.utilities.SampleMetadataStorageRow;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.metadata.ProjectSampleMetadataAjaxController;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIMetadataFileImportService;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIMetadataImportService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ProjectSampleMetadataAjaxControllerTest {
	private ProjectSampleMetadataAjaxController controller;
	private UIMetadataImportService metadataImportService;
	private MessageSource messageSource;
	private ProjectService projectService;
	private SampleService sampleService;
	private MetadataTemplateService metadataTemplateService;
	private UIMetadataFileImportService metadataFileImportService;
	private HttpSession session;

	private final Long PROJECT_ID = 1L;
	private final Long SAMPLE_ID = 1L;
	private final String SAMPLE_NAME = "value2";
	private final String SAMPLE_NAME_COLUMN = "header2";

	@BeforeEach
	public void setUp() {
		session = mock(HttpSession.class);
		messageSource = mock(MessageSource.class);
		projectService = mock(ProjectService.class);
		sampleService = mock(SampleService.class);
		metadataTemplateService = mock(MetadataTemplateService.class);
		metadataFileImportService = new UIMetadataFileImportService(projectService, sampleService);
		metadataImportService = new UIMetadataImportService(messageSource, projectService, sampleService,
				metadataTemplateService, metadataFileImportService);
		controller = new ProjectSampleMetadataAjaxController(metadataImportService);
	}

	private SampleMetadataStorage createSampleMetadataStorage() {
		SampleMetadataStorage expected_storage = new SampleMetadataStorage();
		List<String> headers_list = new ArrayList<>();
		headers_list.add("header1");
		headers_list.add("header2");
		headers_list.add("header3");
		expected_storage.setHeaders(headers_list);
		List<SampleMetadataStorageRow> rows = new ArrayList<>();
		Map<String, String> rowMap = new HashMap<>();
		rowMap.put("header1", "value1");
		rowMap.put("header2", "value2");
		rowMap.put("header3", "value3");
		rows.add(new SampleMetadataStorageRow(rowMap));
		expected_storage.setRows(rows);
		expected_storage.setSampleNameColumn(SAMPLE_NAME_COLUMN);
		return expected_storage;
	}

	private Sample createSample() {
		Sample sample = new Sample();
		sample.setSampleName(SAMPLE_NAME);
		sample.setId(SAMPLE_ID);
		return sample;
	}

	private Project createProject() {
		Project project = new Project();
		project.setId(PROJECT_ID);
		return project;
	}

	@Test
	public void createProjectSampleMetadataTest() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "test.csv", MediaType.TEXT_PLAIN_VALUE,
				"header1,header2,header3\nvalue1,value2,value3".getBytes());
		SampleMetadataStorage stored = createSampleMetadataStorage();

		when(session.getAttribute("pm-" + PROJECT_ID)).thenReturn(stored);

		ResponseEntity<SampleMetadataStorage> response = controller.createProjectSampleMetadata(session, PROJECT_ID,
				file);
		stored = (SampleMetadataStorage) session.getAttribute("pm-" + PROJECT_ID);

		assertEquals(response.getStatusCode(), HttpStatus.OK, "Receive an 200 OK response");
		assertEquals(stored.getRows().size(), 1, "Sample name columns is saved");
		assertEquals(stored.getHeaders().size(), 3, "Sample is saved");
	}

	@Test
	public void setProjectSampleMetadataSampleIdTest() {
		SampleMetadataStorage stored = createSampleMetadataStorage();
		Sample sample = createSample();
		Project project = createProject();

		when(session.getAttribute("pm-" + PROJECT_ID)).thenReturn(stored);
		when(projectService.read(project.getId())).thenReturn(project);
		when(sampleService.getSampleBySampleName(project, sample.getSampleName())).thenReturn(sample);

		ResponseEntity<AjaxResponse> response = controller.setProjectSampleMetadataSampleId(session, PROJECT_ID,
				SAMPLE_NAME_COLUMN);
		stored = (SampleMetadataStorage) session.getAttribute("pm-" + PROJECT_ID);

		assertEquals(response.getStatusCode(), HttpStatus.OK, "Receive an 200 OK response");
		assertEquals(((AjaxSuccessResponse) response.getBody()).getMessage(), "complete", "Receive a complete message");
		assertEquals(stored.getSampleNameColumn(), SAMPLE_NAME_COLUMN, "Sample name columns is saved");
		assertEquals((long) stored.getRow(sample.getSampleName(), SAMPLE_NAME_COLUMN).getFoundSampleId(),
				(long) sample.getId(), "Found sample id is saved");
	}

	@Test
	public void saveProjectSampleMetadataTest() {
		Sample sample = createSample();
		List<String> sampleNames = List.of(sample.getSampleName());
		Locale locale = new Locale("en");
		SampleMetadataStorage stored = createSampleMetadataStorage();
		when(session.getAttribute("pm-" + PROJECT_ID)).thenReturn(stored);
		when(messageSource.getMessage("project.samples.table.sample-id", new Object[] {}, locale))
				.thenReturn("Sample Id");
		when(messageSource.getMessage("project.samples.table.id", new Object[] {}, locale)).thenReturn("ID");
		when(messageSource.getMessage("project.samples.table.modified-date", new Object[] {}, locale))
				.thenReturn("Modified Date");
		when(messageSource.getMessage("project.samples.table.modified", new Object[] {}, locale))
				.thenReturn("Modified On");
		when(messageSource.getMessage("project.samples.table.created-date", new Object[] {}, locale))
				.thenReturn("Created Date");
		when(messageSource.getMessage("project.samples.table.created", new Object[] {}, locale))
				.thenReturn("Created On");
		when(messageSource.getMessage("project.samples.table.coverage", new Object[] {}, locale))
				.thenReturn("Coverage");
		when(messageSource.getMessage("project.samples.table.project-id", new Object[] {}, locale))
				.thenReturn("Project ID");

		ResponseEntity<AjaxResponse> response = controller.saveProjectSampleMetadata(locale, session, PROJECT_ID,
				sampleNames);
		stored = (SampleMetadataStorage) session.getAttribute("pm-" + PROJECT_ID);

		assertEquals(response.getStatusCode(), HttpStatus.OK, "Receive an 200 OK response");
		assertTrue(stored.getRow(sample.getSampleName(), SAMPLE_NAME_COLUMN).isSaved(), "Sample is saved");
	}

	@Test
	public void clearProjectSampleMetadataTest() {
		controller.clearProjectSampleMetadata(session, PROJECT_ID);

		verify(session, times(1)).removeAttribute("pm-" + PROJECT_ID);
	}

	@Test
	public void getProjectSampleMetadataTest() {
		ResponseEntity<SampleMetadataStorage> response = controller.getProjectSampleMetadata(session, PROJECT_ID);

		assertEquals(response.getStatusCode(), HttpStatus.OK, "Receive an 200 OK response");
		verify(session, times(1)).getAttribute("pm-" + PROJECT_ID);
	}
}
