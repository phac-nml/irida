package ca.corefacility.bioinformatics.irida.ria.web.linelist;

import java.util.*;

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
	public LineListController(ProjectService projectService, SampleService sampleService,
			MetadataTemplateService metadataTemplateService) {
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
	 * Get a {@link List} of {@link Map} containing information from {@link MetadataEntry} for all
	 * {@link  Sample}s in a {@link Project}
	 *
	 * @param projectId {@link Long} identifier for a {@link Project}
	 * @return {@link List} of {@link List}s of all {@link Sample} metadata in a {@link Project}
	 */
	@RequestMapping("/entries")
	@ResponseBody
	public List<Map<String, MetadataEntry>> getProjectSamplesMetadataEntries(@RequestParam long projectId) {
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
		List<MetadataTemplateField> fields = metadataTemplateService.getMetadataFieldsForProject(project);

		// Need the sample name.  This will enforce that it is in the first position.
		fields.add(0, new MetadataTemplateField("sampleName", "text"));
		// TODO: (Josh | 2018-04-16) Remove this once Organism is moved into the metadata
		fields.add(1, new MetadataTemplateField("organism", "text"));
		return fields;
	}

	/**
	 * Get a {@link List} of {@link Map} for all {@link  Sample} {@link MetadataEntry}s in a {@link Project}
	 *
	 * @param projectId {@link Long} identifier for a {@link Project}
	 * @return {@link List} of {@link List}s of all {@link Sample} metadata in a {@link Project}
	 */
	private List<Map<String, MetadataEntry>> getAllProjectSamplesMetadataEntries(Long projectId) {
		Project project = projectService.read(projectId);
		List<Join<Project, Sample>> samplesForProject = sampleService.getSamplesForProject(project);
		List<Map<String, MetadataEntry>> result = new ArrayList<>(samplesForProject.size());

		for (Join<Project, Sample> join : samplesForProject) {
			Sample sample = join.getObject();
			Map<String, MetadataEntry> entries = new HashMap<>();

			// Need to have the sample name and Id
			entries.put("sampleName", new MetadataEntry(sample.getLabel(), "text"));
			entries.put("sampleId", new MetadataEntry(String.valueOf(sample.getId()), "number"));

			// TODO: (Josh | 2018-04-16) Remove this once organism is part of the metadata
			entries.put("organism", new MetadataEntry(sample.getOrganism(), "text"));

			Map<MetadataTemplateField, MetadataEntry> sampleMetadata = sample.getMetadata();
			for (MetadataTemplateField field : sampleMetadata.keySet()) {
				entries.put(field.getLabel(), sampleMetadata.getOrDefault(field, new MetadataEntry()));
			}
			result.add(entries);
		}

		return result;
	}
}
