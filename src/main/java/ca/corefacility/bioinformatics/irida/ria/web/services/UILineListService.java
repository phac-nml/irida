package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.ProjectMetadataResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.LineListTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * UI Service to handle LineLists.
 */
@Component
public class UILineListService {
	private ProjectService projectService;
	private SampleService sampleService;
	private MetadataTemplateService metadataTemplateService;

	@Autowired
	public UILineListService(ProjectService projectService, SampleService sampleService,
			MetadataTemplateService metadataTemplateService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.metadataTemplateService = metadataTemplateService;
	}

	/**
	 * Get a {@link Page} of {@link LineListTableModel}
	 *
	 * @param projectId Id of the {@link Project}
	 * @param request   {@link TableRequest}
	 * @return a {@link TableResponse}
	 */
	public TableResponse<LineListTableModel> getProjectSamplesMetadataEntries(long projectId, TableRequest request) {
		Project project = projectService.read(projectId);

		List<Long> lockedSamplesInProject = sampleService.getLockedSamplesInProject(project);

		List<MetadataTemplateField> metadataTemplateFields = metadataTemplateService
				.getPermittedFieldsForCurrentUser(project, true);

		Page<Sample> projectSamples = sampleService.getSamplesWithMetadataForProject(project, metadataTemplateFields,
				PageRequest.of(request.getCurrent(), request.getPageSize(), request.getSort()));
		List<Long> sampleIds = projectSamples.getContent().stream().map(Sample::getId).collect(Collectors.toList());

		Map<Long, Set<MetadataEntry>> metadataForProject;

		// check that we have some fields
		if (!metadataTemplateFields.isEmpty()) {
			// if we have fields, get all the metadata
			ProjectMetadataResponse metadataResponse = sampleService.getMetadataForSamplesInProject(project, sampleIds,
					metadataTemplateFields);

			metadataForProject = metadataResponse.getMetadata();
		} else {
			// if we have no fields, just give an empty map. We'll just show the
			// date fields
			metadataForProject = new HashMap<>();
		}

		List<LineListTableModel> projectSamplesWithMetadata = projectSamples.getContent().stream().map(sample -> {
			Set<MetadataEntry> metadata = null;
			if (metadataForProject.containsKey(sample.getId())) {
				metadata = metadataForProject.get(sample.getId());
			} else {
				metadata = new HashSet<>();
			}

			// check if the project owns the sample
			boolean ownership = !lockedSamplesInProject.contains(sample.getId());

			return new LineListTableModel(project, sample, ownership, metadata);
		}).collect(Collectors.toList());

		return new TableResponse<>(projectSamplesWithMetadata, projectSamples.getTotalElements());
	}
}
