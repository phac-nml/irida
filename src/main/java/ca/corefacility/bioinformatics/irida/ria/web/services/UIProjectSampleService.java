package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.*;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.CreateSampleRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.SampleNameValidationResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.UpdateSampleRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxFormErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxUpdateItemSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.dto.ValidateSampleNameModel;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.dto.ValidateSampleNamesRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.dto.ValidateSampleNamesResponse;
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
	 * Validate a list of sample names
	 *
	 * @param projectId project identifier
	 * @param request   {@link ValidateSampleNamesRequest} details about the sample names to validate
	 * @return a list of validated sample names
	 */
	public ValidateSampleNamesResponse validateSampleNames(Long projectId, ValidateSampleNamesRequest request) {
		List<ValidateSampleNameModel> samples = request.getSamples();
		List<Long> associatedProjectIds = request.getAssociatedProjectIds();
		List<Long> projectIds = new ArrayList<>();
		projectIds.add(projectId);
		if (associatedProjectIds != null) {
			projectIds.addAll(associatedProjectIds);
		}
		List<String> sampleNames = samples.stream().map(ValidateSampleNameModel::getName).collect(Collectors.toList());
		Map<String, List<Long>> foundSampleNames = sampleService.getSampleIdsBySampleNameForProjects(projectIds,
				sampleNames);
		for (ValidateSampleNameModel sample : samples) {
			List<Long> foundSampleIds = foundSampleNames.get(sample.getName());
			sample.setIds(foundSampleIds);
		}
		return new ValidateSampleNamesResponse(samples);
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
	 * Create new samples in a project
	 *
	 * @param requests  Each {@link CreateSampleRequest} contains details about the sample to create
	 * @param projectId Identifier for the current project
	 * @param locale    Users current locale
	 * @return result of creating the sample
	 */
	public ResponseEntity<AjaxResponse> createSamples(CreateSampleRequest[] requests, Long projectId, Locale locale) {
		Map<String, String> errors = new HashMap<>();
		for (CreateSampleRequest request : requests) {
			try {
				createSample(projectId, request);
			} catch (Exception e) {
				errors.put(request.getName(), e.getMessage());
			}
		}
		if (errors.isEmpty()) {
			return ResponseEntity.ok(new AjaxUpdateItemSuccessResponse(
					messageSource.getMessage("server.AddSample.success", null, locale)));
		} else {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new AjaxFormErrorResponse(errors));
		}
	}

	/**
	 * Create a new sample in a project
	 *
	 * @param request   {@link CreateSampleRequest} contains details about the sample to create
	 * @param projectId Identifier for the current project
	 * @return result of creating the sample
	 * @throws EntityNotFoundException if the identifier does not exist in the database
	 */
	@Transactional
	public Long createSample(Long projectId, CreateSampleRequest request) throws EntityNotFoundException {
		Project project = projectService.read(projectId);
		Sample sample = new Sample(request.getName());
		if (!Strings.isNullOrEmpty(request.getOrganism())) {
			sample.setOrganism(request.getOrganism());
		}
		if (!Strings.isNullOrEmpty(request.getDescription())) {
			sample.setDescription(request.getDescription());
		}
		Join<Project, Sample> join = projectService.addSampleToProjectWithoutEvent(project, sample, true);
		if (request.getMetadata() != null) {
			Set<MetadataEntry> metadataEntrySet = request.getMetadata().stream().map(entry -> {
				MetadataTemplateField field = metadataTemplateService.saveMetadataField(
						new MetadataTemplateField(entry.getField(), "text"));
				return new MetadataEntry(entry.getValue(), "text", field);
			}).collect(Collectors.toSet());
			sampleService.mergeSampleMetadata(sample, metadataEntrySet);
		}
		return join.getObject().getId();
	}

	/**
	 * Update samples in a project
	 *
	 * @param requests Each {@link UpdateSampleRequest} contains details about the sample to update
	 * @param locale   Users current locale
	 * @return result of creating the samples
	 */
	public ResponseEntity<AjaxResponse> updateSamples(UpdateSampleRequest[] requests, Locale locale) {
		Map<String, String> errors = new HashMap<>();
		for (UpdateSampleRequest request : requests) {
			try {
				updateSample(request);
			} catch (Exception e) {
				errors.put(request.getName(), e.getMessage());
			}
		}
		if (errors.isEmpty()) {
			return ResponseEntity.ok(new AjaxUpdateItemSuccessResponse(
					messageSource.getMessage("server.AddSample.success", null, locale)));
		} else {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new AjaxFormErrorResponse(errors));
		}
	}

	/**
	 * Update a sample in a project
	 *
	 * @param request {@link UpdateSampleRequest} contains details about the sample to update
	 * @return result of creating the sample
	 * @throws EntityNotFoundException      if the identifier does not exist in the database
	 * @throws ConstraintViolationException if the entity being updated contains constraint violations
	 */
	@Transactional
	public Sample updateSample(UpdateSampleRequest request)
			throws EntityNotFoundException, ConstraintViolationException {
		Long sampleId = request.getSampleId();
		Sample sample = sampleService.read(sampleId);
		sample.setSampleName(request.getName());
		if (!Strings.isNullOrEmpty(request.getOrganism())) {
			sample.setOrganism(request.getOrganism());
		}
		if (request.getDescription() != null) {
			sample.setDescription(request.getDescription());
		}
		if (request.getMetadata() != null) {
			Set<MetadataEntry> metadataEntrySet = request.getMetadata().stream().map(entry -> {
				MetadataTemplateField field = metadataTemplateService.saveMetadataField(
						new MetadataTemplateField(entry.getField(), "text"));
				return new MetadataEntry(entry.getValue(), "text", field);
			}).collect(Collectors.toSet());
			sampleService.updateSampleMetadata(sample, metadataEntrySet);
		}
		return sampleService.update(sample);

	}
}
