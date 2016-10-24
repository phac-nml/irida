package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleMetadata;
import ca.corefacility.bioinformatics.irida.ria.web.components.linelist.LineListField;
import ca.corefacility.bioinformatics.irida.ria.web.components.linelist.LineListTemplate;
import ca.corefacility.bioinformatics.irida.ria.web.components.linelist.LineListTemplates;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

@Controller
@RequestMapping("/projects/{projectId}/linelist")
public class ProjectLineListController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectLineListController.class);

	private final ProjectService projectService;
	private final SampleService sampleService;
	private final ProjectControllerUtils projectControllerUtils;
	private LineListTemplates templates;

	@Autowired
	public ProjectLineListController(ProjectService projectService, SampleService sampleService,
			ProjectControllerUtils utils) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.projectControllerUtils = utils;
		this.templates = new LineListTemplates();
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
		model.addAttribute("templates", templates.getTemplateNames());
		return "projects/project_linelist";
	}

	/**
	 * Get the page to create new linelist templates
	 *
	 * @param projectId
	 * 		{@link Long} identifier for the current {@link Project}
	 * @param model
	 * 		{@link Model}
	 * @param principal
	 * 		{@link Principal}
	 *
	 * @return {@link String} path to the page.
	 */
	@RequestMapping("/linelist-templates")
	public String getLinelistTemplatePage(@PathVariable Long projectId, Model model, Principal principal) {
		// Set up the template information
		Project project = projectService.read(projectId);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute("templates", templates.getTemplateNames());
		return "projects/project_linelist_template";
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
	public Map<String, Object> getLinelistTemplate(
			@RequestParam(required = false, defaultValue = "default") String template) {
		return ImmutableMap.of("template", templates.getTemplate(template));
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
		List<LineListField> currentTemplate;
		if (templates.doesTemplateExists(template)) {
			currentTemplate = templates.getTemplate(template);
		} else {
			currentTemplate = templates.getTemplate("default");
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
			for (LineListField field : currentTemplate.subList(2, currentTemplate.size())) {
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

	/**
	 * Get a list of all fields that exist on the templates requested
	 *
	 * @param template
	 * 		{@link String} name of the template whose field are needed.
	 *
	 * @return {@link Map} containing a list of all the unique fields.
	 */
	@RequestMapping("/linelist-templates/current")
	@ResponseBody
	public Map<String, Object> getExistingTemplateFields(@RequestParam String template) {
		return ImmutableMap.of("fields", templates.getTemplate(template));
	}

	@RequestMapping(value = "/linelist-templates/save-template", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> saveLinelistTemplate(
			@RequestBody String template) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			LineListTemplate lineListTemplate = mapper.readValue(template, LineListTemplate.class);
			// Set up the template information
			templates.addTemplate(lineListTemplate.getName(), lineListTemplate.getFields());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ImmutableMap.of("success", true);
	}
}
