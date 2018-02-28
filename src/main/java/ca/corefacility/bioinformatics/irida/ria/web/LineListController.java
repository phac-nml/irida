package ca.corefacility.bioinformatics.irida.ria.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.ria.web.models.UIMetadataEntryModel;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * This controller is responsible for AJAX handling for the line list page, which displays sample metatdata.
 */
@Controller
@RequestMapping("/linelist")
public class LineListController {
	private ProjectService projectService;
	private SampleService sampleService;
	private MetadataTemplateService metadataTemplateService;

	@Autowired
	public LineListController(ProjectService projectService, SampleService sampleService, MetadataTemplateService metadataTemplateService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.metadataTemplateService = metadataTemplateService;
	}

	/**
	 * Get a list of all {@link MetadataTemplateField}s on a {@link Project}
	 *
	 * @param projectId {@link Long} identifier for a {@link Project}
	 * @return {@link List} of {@link MetadataTemplateField}
	 */
	@RequestMapping("/fields")
	@ResponseBody
	public List<MetadataTemplateField> getProjectMetadataTemplateFields(@RequestParam long projectId) {
		return getAllProjectMetadataFields(projectId);
	}

	/**
	 * Get a {@link List} of {@link UIMetadataEntryModel} for all {@link  Sample}s in a {@link Project}
	 *
	 * @param projectId {@link Long} identifier for a {@link Project}
	 * @return {@link List} of {@link List}s of all {@link Sample} metadata in a {@link Project}
	 */
	@RequestMapping("/entries")
	@ResponseBody
	public List<List<UIMetadataEntryModel>> getProjectSamplesMetadataEntries(@RequestParam long projectId) {
		return getAllProjectSamplesMetadataEntries(projectId);
	}

	/**
	 * Get the template the the line list table.  This becomes the table headers.
	 *
	 * @param projectId {@link Long} identifier of the current {@link Project}
	 * @return {@link Set} containing unique metadata fields
	 */
	private List<MetadataTemplateField> getAllProjectMetadataFields(Long projectId) {
		Project project = projectService.read(projectId);
		return metadataTemplateService.getMetadataFieldsForProject(project);
	}

	/**
	 * Get a {@link List} of {@link UIMetadataEntryModel} for all {@link  Sample}s in a {@link Project}
	 *
	 * @param projectId {@link Long} identifier for a {@link Project}
	 * @return {@link List} of {@link List}s of all {@link Sample} metadata in a {@link Project}
	 */
	private List<List<UIMetadataEntryModel>> getAllProjectSamplesMetadataEntries(Long projectId) {
		Project project = projectService.read(projectId);
		List<Join<Project, Sample>> samplesForProject = sampleService.getSamplesForProject(project);

		List<List<UIMetadataEntryModel>> result = new ArrayList<>(samplesForProject.size());

		for (Join<Project, Sample> join : samplesForProject) {
			Sample sample = join.getObject();
			List<UIMetadataEntryModel> sampleModels = new ArrayList<>();
			Map<MetadataTemplateField, MetadataEntry> sampleMetadata = sample.getMetadata();
			sampleMetadata.forEach((metadataTemplateField, metadataEntry) -> sampleModels.add(new UIMetadataEntryModel(project, sample, metadataEntry, metadataTemplateField)));
			result.add(sampleModels);
		}

		return result;
	}
}
