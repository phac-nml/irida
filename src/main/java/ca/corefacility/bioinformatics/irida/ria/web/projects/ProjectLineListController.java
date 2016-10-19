package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleMetadata;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

@Controller @RequestMapping("/projects/{projectId}/linelist")
public class ProjectLineListController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectLineListController.class);

	private static final List<String> DEFAULT_TEMPLATE = ImmutableList.of(
			"identifier",
			"label",
			"PFGE-XbaI-pattern",
			"PFGE-BlnI-pattern",
			"NLEP #",
			"SubmittedNumber",
			"Province",
			"SourceSite",
			"SourceType",
			"PatientAge",
			"PatientSex",
			"Genus",
			"Serotype",
			"ReceivedDate",
			"UploadDate",
			"IsolatDate",
			"SourceCity",
			"UploadModifiedDate",
			"Comments",
			"Outbreak",
			"Phagetype",
			"Traveled_To",
			"Exposure");

	private static final List<String> INTERESTING_TEMPLATE = ImmutableList.of(
			"identifier",
			"label",
			"NLEP #",
			"Province",
			"SourceType",
			"Genus",
			"Serotype");

	private static final Map<String, List> TEMPLATES = ImmutableMap.of(
			"default", DEFAULT_TEMPLATE,
			"interesting", INTERESTING_TEMPLATE
	);


	private final ProjectService projectService;
	private final SampleService sampleService;
	private final ProjectControllerUtils projectControllerUtils;
	private MessageSource messageSource;

	@Autowired
	public ProjectLineListController(ProjectService projectService, SampleService sampleService,
			ProjectControllerUtils utils, MessageSource messageSource) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.projectControllerUtils = utils;
		this.messageSource = messageSource;
	}

	@RequestMapping("")
	public String getLineListPage(@PathVariable Long projectId, Model model, Principal principal) {
		// Set up the template information
		Project project = projectService.read(projectId);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute("activeNav", "linelist");
		model.addAttribute("templates", TEMPLATES.keySet());
		return "projects/project_linelist";
	}

	@RequestMapping("/available-templates")
	@ResponseBody
	public Map<String, Object> getAvailableProjectTemplates(@PathVariable Long projectId) {
		return ImmutableMap.of("templates", TEMPLATES.keySet());
	}

	@RequestMapping("/mt")
	@ResponseBody
	public Map<String, Object> getLinelistTemplate(@PathVariable Long projectId, @RequestParam(required = false, defaultValue = "default") String template) {
		if (TEMPLATES.containsKey(template)) {
			return ImmutableMap.of("template", TEMPLATES.get(template));
		} else {
			return ImmutableMap.of("template", ImmutableList.of());
		}
	}

	@RequestMapping("/metadata")
	@ResponseBody
	public Map<String, Object> getLinelistMetadata(@PathVariable Long projectId, @RequestParam(required = false, defaultValue = "default") String template) {
		Project project = projectService.read(projectId);
		List<Join<Project, Sample>> projectSamples = sampleService.getSamplesForProject(project);
		List<String> currentTemplate;
		if (TEMPLATES.containsKey(template)) {
			currentTemplate = TEMPLATES.get(template);
		} else {
			currentTemplate = TEMPLATES.get("default");
		}

		List<Map<String, Object>> metadata = new ArrayList<>();
		for (Join<Project, Sample> join : projectSamples) {
			Sample sample = join.getObject();
			SampleMetadata sampleMetadata = sampleService.getMetadataForSample(sample);
			// TODO: This is where the project template should be applied.  For now return everything.
			Map<String, Object> data;
			if(sampleMetadata != null) {
				data = sampleMetadata.getMetadata();
			} else {
				data = new HashMap();
			}
			Map<String, Object> md = new HashMap<>();
			md.put("identifier", sample.getId());
			md.put("label", sample.getLabel());
			// Every template is expected to start with the sample identifier and label
			for (String header : currentTemplate.subList(2, currentTemplate.size())) {
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
