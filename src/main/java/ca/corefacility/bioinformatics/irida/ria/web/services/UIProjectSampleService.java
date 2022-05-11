package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.CreateSampleRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.SampleNameValidationResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxCreateItemSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.base.Strings;

/**
 * UI Service to handle samples within a project.
 */
@Component
public class UIProjectSampleService {

	private final ProjectService projectService;
	private final SampleService sampleService;
	private final MessageSource messageSource;

	@Autowired
	public UIProjectSampleService(ProjectService projectService, SampleService sampleService,
			MessageSource messageSource) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.messageSource = messageSource;
	}

	/**
	 * Validate a sample name to ensure can be stored correctly.  Must be: - at least 3 characters Long, - no special
	 * characters (including spaces) - name must not already exist for a sample in the project
	 *
	 * @param name      Name to validate.
	 * @param projectId current project identifier
	 * @param locale    current users locale
	 * @return result of the validation.
	 */
	public ResponseEntity<SampleNameValidationResponse> validateNewSampleName(String name, Long projectId,
			Locale locale) {
		int SAMPLE_NAME_MIN_LENGTH = 3;

		// Make sure it has the correct length
		if (name.length() <= SAMPLE_NAME_MIN_LENGTH) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY.value())
					.body(new SampleNameValidationResponse("error",
							messageSource.getMessage("server.AddSample.error.length", new Object[] {}, locale)));
		}

		/*
		This is copied from the previous client side validation.
		 */
		if (!name.matches("[A-Za-z\\d-_!@#$%~`]+")) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY.value())
					.body(new SampleNameValidationResponse("error", messageSource.getMessage("server.AddSample.error.special.characters", new Object[] {}, locale)));
		}

		// Check to see if the sample name already exists.
		try {

			Project project = projectService.read(projectId);
			sampleService.getSampleBySampleName(project, name);
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(new SampleNameValidationResponse("error",
							messageSource.getMessage("server.AddSample.error.exists", new Object[] {}, locale)));

		} catch (EntityNotFoundException e) {
			return ResponseEntity.ok(new SampleNameValidationResponse("success", null));
		}

	}

	/**
	 * Create a new sample in a project
	 *
	 * @param request   {@link CreateSampleRequest} details about the sample to create
	 * @param projectId Identifier for the current project
	 * @param locale    Users current locale
	 * @return result of creating the sample
	 */
	public ResponseEntity<AjaxResponse> createSample(CreateSampleRequest request, Long projectId, Locale locale) {
		Project project = projectService.read(projectId);
		try {
			Sample sample = new Sample(request.getName());
			if (!Strings.isNullOrEmpty(request.getOrganism())) {
				sample.setOrganism(request.getOrganism());
			}
			Join<Project, Sample> join = projectService.addSampleToProject(project, sample, true);
			return ResponseEntity.ok(new AjaxCreateItemSuccessResponse(join.getObject().getId()));
		} catch (EntityNotFoundException e) {
			return ResponseEntity.ok(new AjaxErrorResponse(
					messageSource.getMessage("server.AddSample.error.exists", new Object[] {}, locale)));
		}
	}
}
