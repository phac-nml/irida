package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.*;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.dto.MetadataEntryModel;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectSampleService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UIProjectSampleServiceTest {
	private UIProjectSampleService service;
	private ProjectService projectService;
	private SampleService sampleService;
	private MetadataTemplateService metadataTemplateService;
	private MessageSource messageSource;

	private Sample sample;
	private Join<Project, Sample> join;

	// DATA
	private final Long PROJECT_1_ID = 1L;
	private final Long SAMPLE_1_ID = 11L;
	private final Project PROJECT_1 = new Project("PROJECT_1");
	private final Sample SAMPLE_1 = new Sample("SAMPLE_1");
	private final String BAD_NAME = "bad name with spaces";
	private final String SHORT_NAME = "sho";
	private final String GOOD_NAME = "good_name";
	private final String ORGANISM = "organism";
	private final String DESCRIPTION = "this is a description";

	@BeforeEach
	public void setUp() {
		projectService = mock(ProjectService.class);
		sampleService = mock(SampleService.class);
		metadataTemplateService = mock(MetadataTemplateService.class);
		messageSource = mock(MessageSource.class);

		service = new UIProjectSampleService(projectService, sampleService, metadataTemplateService, messageSource);

		when(projectService.read(PROJECT_1_ID)).thenReturn(PROJECT_1);
		when(sampleService.read(SAMPLE_1_ID)).thenReturn(SAMPLE_1);
		when(sampleService.getSampleBySampleName(PROJECT_1, GOOD_NAME)).thenThrow(
				new EntityNotFoundException("Sample not found"));
		sample = new Sample(GOOD_NAME);
		sample.setId(SAMPLE_1_ID);
		join = new ProjectSampleJoin(PROJECT_1, sample, true);
		when(projectService.addSampleToProject(any(Project.class), any(Sample.class), any(Boolean.class))).thenReturn(
				join);
		when(projectService.addSampleToProjectWithoutEvent(any(Project.class), any(Sample.class),
				any(Boolean.class))).thenReturn(join);
	}

	@Test
	public void testValidateNewSampleName() {
		ResponseEntity<SampleNameValidationResponse> response = service.validateNewSampleName("sampleName",
				PROJECT_1_ID, Locale.ENGLISH);
		response = service.validateNewSampleName(GOOD_NAME, 1L, Locale.ENGLISH);
		assertEquals(HttpStatus.OK, response.getStatusCode(), "Good sample name should return OK");

		response = service.validateNewSampleName(BAD_NAME, 1L, Locale.ENGLISH);
		assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode(),
				"Sample with bad characters should return UNPROCESSABLE_ENTITY");

		response = service.validateNewSampleName(SHORT_NAME, 1L, Locale.ENGLISH);
		assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode(),
				"Sample names not long enough should return UNPROCESSABLE_ENTITY");
	}

	@Test
	public void testCreateSample() {
		CreateSampleRequest[] requests = { new CreateSampleRequest(GOOD_NAME, null) };
		Map<String, Object> responses = service.createSamples(requests, PROJECT_1_ID);
		long errorCount = responses.entrySet()
				.stream()
				.filter(response -> ((SampleResponse) response.getValue()).isError())
				.count();
		assertEquals(0, errorCount, "Sample should be created");
	}

	@Test
	public void testCreateSampleWithOrganism() {
		CreateSampleRequest[] requests = { new CreateSampleRequest(GOOD_NAME, ORGANISM) };
		Map<String, Object> responses = service.createSamples(requests, PROJECT_1_ID);
		long errorCount = responses.entrySet()
				.stream()
				.filter(response -> ((SampleResponse) response.getValue()).isError())
				.count();
		assertEquals(0, errorCount, "Sample should be created");
	}

	@Test
	public void testCreateSampleWithDescription() {
		CreateSampleRequest[] requests = { new CreateSampleRequest(GOOD_NAME, null, DESCRIPTION, null) };
		Map<String, Object> responses = service.createSamples(requests, PROJECT_1_ID);
		long errorCount = responses.entrySet()
				.stream()
				.filter(response -> ((SampleResponse) response.getValue()).isError())
				.count();
		assertEquals(0, errorCount, "Sample should be created");
	}

	@Test
	public void testCreateSampleWithOrganismAndDescription() {
		CreateSampleRequest[] requests = { new CreateSampleRequest(GOOD_NAME, ORGANISM, DESCRIPTION, null) };
		Map<String, Object> responses = service.createSamples(requests, PROJECT_1_ID);
		long errorCount = responses.entrySet()
				.stream()
				.filter(response -> ((SampleResponse) response.getValue()).isError())
				.count();
		assertEquals(0, errorCount, "Sample should be created");
	}

	@Test
	public void testCreateSampleWithMetadata() {
		List<MetadataEntryModel> metadata = new ArrayList<>();
		metadata.add(new MetadataEntryModel("field1", "value1"));
		metadata.add(new MetadataEntryModel("field2", "value2"));
		CreateSampleRequest[] requests = { new CreateSampleRequest(GOOD_NAME, null, null, metadata) };
		Map<String, Object> responses = service.createSamples(requests, PROJECT_1_ID);
		long errorCount = responses.entrySet()
				.stream()
				.filter(response -> ((SampleResponse) response.getValue()).isError())
				.count();
		verify(sampleService, times(1)).mergeSampleMetadata(any(), any());
		assertEquals(0, errorCount, "Sample should be created");
	}

	@Test
	public void testUpdateSampleWithMetadata() {
		List<MetadataEntryModel> metadata = new ArrayList<>();
		metadata.add(new MetadataEntryModel("field1", "value1"));
		metadata.add(new MetadataEntryModel("field2", "value2"));
		UpdateSampleRequest[] requests = { new UpdateSampleRequest(SAMPLE_1_ID, GOOD_NAME, null, null, metadata) };
		Map<String, Object> responses = service.updateSamples(requests);
		long errorCount = responses.entrySet()
				.stream()
				.filter(response -> ((SampleErrorResponse) response.getValue()).isError())
				.count();
		verify(sampleService, times(1)).mergeSampleMetadata(any(), any());
		assertEquals(0, errorCount, "Sample should be updated");
	}

}
