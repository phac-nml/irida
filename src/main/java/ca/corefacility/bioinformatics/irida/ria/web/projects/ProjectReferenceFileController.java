package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;

import com.google.common.collect.ImmutableMap;

/**
 * Controller for ajax request dealing with project reference files.
 *
 */
@Controller
@RequestMapping(value = "/projects")
public class ProjectReferenceFileController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectReferenceFileController.class);
	private final ProjectService projectService;
	private final ReferenceFileService referenceFileService;
	private final ProjectControllerUtils projectControllerUtils;
	private final MessageSource messageSource;

	@Autowired
	public ProjectReferenceFileController(ProjectService projectService, ReferenceFileService referenceFileService,
			ProjectControllerUtils projectControllerUtils, MessageSource messageSource) {
		this.projectService = projectService;
		this.referenceFileService = referenceFileService;
		this.projectControllerUtils = projectControllerUtils;
		this.messageSource = messageSource;
	}

	@RequestMapping(value = "/{projectId}/settings/referenceFiles", method = RequestMethod.GET)
	public String getProjectReferenceFilesPage(final Model model, final Principal principal,
			@PathVariable long projectId) {
		Project project = projectService.read(projectId);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);

		model.addAttribute("project", project);
		model.addAttribute(ProjectsController.ACTIVE_NAV, ProjectSettingsController.ACTIVE_NAV_SETTINGS);
		model.addAttribute("page", "referenceFiles");
		return "projects/project_settings";
	}

	@RequestMapping("/{projectId}/settings/ajax/reference/all")
	public @ResponseBody Map<String, Object> getReferenceFilesForProject(@PathVariable Long projectId, Locale locale) {
		Project project = projectService.read(projectId);
		// Let's add the reference files
		List<Join<Project, ReferenceFile>> joinList = referenceFileService.getReferenceFilesForProject(project);
		List<Map<String, Object>> files = new ArrayList<>();
		for (Join<Project, ReferenceFile> join : joinList) {
			ReferenceFile file = join.getObject();
			Map<String, Object> map = new HashMap<>();
			map.put("id", file.getId().toString());
			map.put("label", file.getLabel());
			map.put("createdDate", file.getCreatedDate());
			Path path = file.getFile();
			try {
				map.put("size", Files.size(path));
			} catch (IOException e) {
				logger.error("Cannot find the size of file " + file.getLabel());
				map.put("size", messageSource.getMessage("projects.reference-file.not-found", new Object[] {}, locale));
			}
			files.add(map);
		}
		return ImmutableMap.of("files", files);
	}
}
