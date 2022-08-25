package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.CreateSampleRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.SampleNameValidationResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxCreateItemSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxUpdateItemSuccessResponse;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.base.Strings;

/**
 * UI Service to handle samples within a project.
 */
@Component
public class UIProjectSampleService {

	private final ProjectService projectService;
	private final SampleService sampleService;
	private final MetadataTemplateService metadataTemplateService;
	private final MessageSource messageSource;

	@Autowired
	public UIProjectSampleService(ProjectService projectService, SampleService sampleService,
			MetadataTemplateService metadataTemplateService, MessageSource messageSource) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.metadataTemplateService = metadataTemplateService;
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
					.body(new SampleNameValidationResponse("error",
							messageSource.getMessage("server.AddSample.error.special.characters", new Object[] {},
									locale)));
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
			if (!Strings.isNullOrEmpty(request.getDescription())) {
				sample.setDescription(request.getDescription());
			}
			Join<Project, Sample> join = projectService.addSampleToProject(project, sample, true);
			if (request.getMetadata() != null) {
				Set<MetadataEntry> metadataEntrySet = request.getMetadata().stream().map(entry -> {
					MetadataTemplateField field = metadataTemplateService.saveMetadataField(
							new MetadataTemplateField(entry.getField(), "text"));
					return new MetadataEntry(entry.getValue(), "text", field);
				}).collect(Collectors.toSet());
				sampleService.mergeSampleMetadata(sample, metadataEntrySet);
			}
			return ResponseEntity.ok(new AjaxCreateItemSuccessResponse(join.getObject().getId()));
		} catch (EntityNotFoundException e) {
			return ResponseEntity.ok(new AjaxErrorResponse(
					messageSource.getMessage("server.AddSample.error.exists", new Object[] {}, locale)));
		}
	}

	/**
	 * Update a sample in a project
	 *
	 * @param request  {@link CreateSampleRequest} details about the sample to create
	 * @param sampleId Identifier for the sample
	 * @param locale   Users current locale
	 * @return result of creating the sample
	 */
	public ResponseEntity<AjaxResponse> updateSample(CreateSampleRequest request, Long sampleId, Locale locale) {
		Map<String, Object> updatedValues = new HashMap<>();
		Sample sample = sampleService.read(sampleId);
		if (!Strings.isNullOrEmpty(request.getName())) {
			updatedValues.put("sampleName", request.getName());
		}
		if (!Strings.isNullOrEmpty(request.getOrganism())) {
			updatedValues.put("organism", request.getOrganism());
		}
		if (!Strings.isNullOrEmpty(request.getDescription())) {
			updatedValues.put("description", request.getDescription());
		}
		if (request.getMetadata() != null) {
			Set<MetadataEntry> metadataEntrySet = request.getMetadata().stream().map(entry -> {
				MetadataTemplateField field = metadataTemplateService.saveMetadataField(
						new MetadataTemplateField(entry.getField(), "text"));
				return new MetadataEntry(entry.getValue(), "text", field);
			}).collect(Collectors.toSet());
			sampleService.updateSampleMetadata(sample, metadataEntrySet);
		}
		sampleService.updateFields(sampleId, updatedValues);
		return ResponseEntity.ok(new AjaxUpdateItemSuccessResponse("SUCCESS"));
	}
}
