package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleMetadata;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

@Controller
@RequestMapping("/projects/{projectId}/linelist")
public class ProjectLineListController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectLineListController.class);

	private static final List<MetadataField> DEFAULT_TEMPLATE = ImmutableList
			.of(new MetadataField("identifier", "text"), new MetadataField("label", "text"),
					new MetadataField("PFGE-XbaI-pattern", "text"), new MetadataField("PFGE-BlnI-pattern", "text"),
					new MetadataField("NLEP #", "text"), new MetadataField("SubmittedNumber", "text"),
					new MetadataField("Province", "text"), new MetadataField("SourceSite", "text"),
					new MetadataField("SourceType", "text"), new MetadataField("PatientAge", "text"),
					new MetadataField("PatientSex", "text"), new MetadataField("Genus", "text"),
					new MetadataField("Serotype", "text"), new MetadataField("ReceivedDate", "text"),
					new MetadataField("UploadDate", "text"), new MetadataField("IsolatDate", "text"),
					new MetadataField("SourceCity", "text"), new MetadataField("UploadModifiedDate", "text"),
					new MetadataField("Comments", "text"), new MetadataField("Outbreak", "text"),
					new MetadataField("Phagetype", "text"), new MetadataField("Traveled_To", "text"),
					new MetadataField("Exposure", "text"));

	private static final List<MetadataField> INTERESTING_TEMPLATE = ImmutableList
			.of(new MetadataField("identifier", "text"), new MetadataField("label", "text"),
					new MetadataField("NLEP #", "text"), new MetadataField("Province", "text"),
					new MetadataField("SourceType", "text"), new MetadataField("Genus", "text"),
					new MetadataField("Serotype", "text"));

	private static final Map<String, List> TEMPLATES = ImmutableMap
			.of("default", DEFAULT_TEMPLATE, "interesting", INTERESTING_TEMPLATE);

	private final ProjectService projectService;
	private final SampleService sampleService;
	private final ProjectControllerUtils projectControllerUtils;

	@Autowired
	public ProjectLineListController(ProjectService projectService, SampleService sampleService,
			ProjectControllerUtils utils) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.projectControllerUtils = utils;
	}

	/**
	 * Get the page to display the project samples linelist.
	 *
	 * @param projectId
	 * 		{@link Long} identifier for the current {@link Project}
	 * @param model
	 * 		{@link Model}
	 * @param principal
	 * 		{@link Principal} currently logged in user.
	 *
	 * @return {@link String} path to the current page.
	 */
	@RequestMapping("")
	public String getLineListPage(@PathVariable Long projectId, Model model, Principal principal) {
		// Set up the template information
		Project project = projectService.read(projectId);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute("activeNav", "linelist");
		model.addAttribute("templates", TEMPLATES.keySet());
		return "projects/project_linelist";
	}

	/**
	 * Get the template the the line list table.  This becomes the table headers.
	 *
	 * @param projectId
	 * 		{@link Long} identifier for the current {@link Project}.
	 * @param template
	 * 		{@link String} name of the template to get.
	 *
	 * @return {@link Map} containing the template.
	 */
	@RequestMapping("/mt")
	@ResponseBody
	public Map<String, Object> getLinelistTemplate(@PathVariable Long projectId,
			@RequestParam(required = false, defaultValue = "default") String template) {
		if (TEMPLATES.containsKey(template)) {
			return ImmutableMap.of("template", TEMPLATES.get(template));
		} else {
			return ImmutableMap.of("template", ImmutableList.of());
		}
	}

	/**
	 * Get the metadata for the table that matches the current template.
	 *
	 * @param projectId
	 * 		{@link Long} identifier for the current {@link Project}.
	 * @param template
	 * 		{@link String} name of the template to metadata for.
	 *
	 * @return {@link Map} of all the metadata.
	 */
	@RequestMapping("/metadata")
	@ResponseBody
	public Map<String, Object> getLinelistMetadata(@PathVariable Long projectId,
			@RequestParam(required = false, defaultValue = "default") String template) {
		Project project = projectService.read(projectId);
		List<Join<Project, Sample>> projectSamples = sampleService.getSamplesForProject(project);
		List<MetadataField> currentTemplate;
		if (TEMPLATES.containsKey(template)) {
			currentTemplate = TEMPLATES.get(template);
		} else {
			currentTemplate = TEMPLATES.get("default");
		}

		List<Map<String, Object>> metadata = new ArrayList<>();
		for (Join<Project, Sample> join : projectSamples) {
			Sample sample = join.getObject();
			SampleMetadata sampleMetadata = sampleService.getMetadataForSample(sample);

			Map<String, Object> data;
			if (sampleMetadata != null) {
				data = sampleMetadata.getMetadata();
			} else {
				data = new HashMap();
			}
			Map<String, Object> md = new HashMap<>();
			md.put("identifier", sample.getId());
			md.put("label", sample.getLabel());
			// Every template is expected to start with the sample identifier and label
			for (MetadataField field : currentTemplate.subList(2, currentTemplate.size())) {
				String header = field.getLabel();
				if (data.containsKey(header)) {
					md.put(header, data.get(header));
				} else {
					md.put(header, "");
				}
			}
			metadata.add(md);
		}
		return ImmutableMap.of("metadata", metadata);
	}
}
