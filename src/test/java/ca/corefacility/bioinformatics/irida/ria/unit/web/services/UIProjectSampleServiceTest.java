package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.SampleNameValidationResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectSampleService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UIProjectSampleServiceTest {
	private UIProjectSampleService service;
	private ProjectService projectService;
	private SampleService sampleService;

	// DATA
	private final Long PROJECT_1_ID = 1L;
	private final Project PROJECT_1 = new Project("PROJECT_1");
	private final String BAD_NAME = "bad name with spaces";
	private final String SHORT_NAME = "sho";
	private final String GOOD_NAME = "good_name";

	@BeforeEach
	public void setUp() {
		projectService = mock(ProjectService.class);
		sampleService = mock(SampleService.class);
		MessageSource messageSource = mock(MessageSource.class);

		service = new UIProjectSampleService(projectService, sampleService, messageSource);

		when(projectService.read(PROJECT_1_ID)).thenReturn(PROJECT_1);
		when(sampleService.getSampleBySampleName(PROJECT_1, GOOD_NAME)).thenThrow(
				new EntityNotFoundException("Sample not found"));
	}

	@Test
	public void testValidateNewSampleName() {
		ResponseEntity<SampleNameValidationResponse> response = service.validateNewSampleName("sampleName",
				PROJECT_1_ID, Locale.ENGLISH);
		response = service.validateNewSampleName(GOOD_NAME, 1L, Locale.ENGLISH);
		assertEquals(HttpStatus.OK, response.getStatusCode(), "Good sample name should return OK");

		response = service.validateNewSampleName(BAD_NAME, 1L, Locale.ENGLISH);
		assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode()), "Sample with bad characters should return UNPROCESSABLE_ENTITY");

		response = service.validateNewSampleName(SHORT_NAME, 1L, Locale.ENGLISH);
		assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode()), "Sample names not long enough should return UNPROCESSABLE_ENTITY");
	}
}
