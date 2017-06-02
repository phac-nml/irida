package ca.corefacility.bioinformatics.irida.ria.web.projects.metadata;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.ria.web.models.UIMetadataTemplate;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;

@Controller
@RequestMapping("/projects/{projectId}/metadata-templates")
public class ProjectSamplesMetadataTemplateController {
    private ProjectService projectService;
    private MetadataTemplateService metadataTemplateService;
    private ProjectControllerUtils projectControllerUtils;

    @Autowired
    public ProjectSamplesMetadataTemplateController(ProjectService projectService,
                                                    ProjectControllerUtils projectControllerUtils,
                                                    MetadataTemplateService metadataTemplateService) {
        this.projectService = projectService;
        this.projectControllerUtils = projectControllerUtils;
        this.metadataTemplateService = metadataTemplateService;
    }

    /**
     * Get the page to create a new {@link MetadataTemplate}
     *
     * @param projectId {@link Long} identifier for a {@link Project}
     * @param model     {@link Model} spring page model
     * @param principal {@link Principal} currently logged in user
     * @return {@link String} path to the new template page
     */
    @RequestMapping("/new")
    public String getMetadataTemplateListPage(@PathVariable Long projectId,
                                              Model model,
                                              Principal principal) {
        Project project = projectService.read(projectId);
        model.addAttribute("template", new UIMetadataTemplate());
        projectControllerUtils.getProjectTemplateDetails(model, principal, project);
        return "projects/project_samples_metadata_template";
    }

    /**
     * Get a the page for a specific {@link MetadataTemplate}
     *
     * @param projectId  {@link Long} identifier for a {@link Project}
     * @param templateId {@link Long} identifier for a {@link MetadataTemplate}
     * @param principal  {@link Principal} currently logged in user
     * @param model      {@link Model} spring page model
     * @return {@link String} path to template page
     */
    @RequestMapping("/{templateId}")
    public String getMetadataTemplatePage(@PathVariable Long projectId,
                                          @PathVariable Long templateId,
                                          Principal principal,
                                          Model model) {
        Project project = projectService.read(projectId);
        projectControllerUtils.getProjectTemplateDetails(model, principal, project);
        MetadataTemplate metadataTemplate = metadataTemplateService.read(templateId);
        UIMetadataTemplate template = new UIMetadataTemplate(metadataTemplate);
        model.addAttribute("template", template);
        return "projects/project_samples_metadata_template";
    }

    /**
     * Save or update a {@link MetadataTemplate} within a {@link Project}
     *
     * @param projectId  {@link Long} identifier for a {@link Project}
     * @param templateId {@link Long} identifier for a {@link MetadataTemplate}
     * @return {@link String} redirects to the template page.
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveMetadataTemplate(@PathVariable Long projectId, UIMetadataTemplate template) {
        Project project = projectService.read(projectId);
        List<MetadataTemplateField> metadataFields = new ArrayList<>();
        for (String field : template.getFields()) {
            MetadataTemplateField metadataTemplateField = metadataTemplateService.readMetadataFieldByLabel(field);
            if (metadataTemplateField == null) {
                metadataTemplateField = new MetadataTemplateField(field, "text");
                metadataTemplateService.saveMetadataField(metadataTemplateField);
            }
            metadataFields.add(metadataTemplateField);
        }

        MetadataTemplate metadataTemplate;
        if (template.getId() != null) {
            metadataTemplate = metadataTemplateService.read(template.getId());
            metadataTemplate.setName(template.getName());
            metadataTemplate.setFields(metadataFields);
            metadataTemplateService.updateMetadataTemplateInProject(project, metadataTemplate);
        } else {
            ProjectMetadataTemplateJoin projectMetadataTemplateJoin = metadataTemplateService
                    .createMetadataTemplateInProject(new MetadataTemplate(template.getName(), metadataFields), project);
            metadataTemplate = projectMetadataTemplateJoin.getObject();
        }
        return "redirect:/projects/" + projectId + "/metadata-templates/" + metadataTemplate.getId();
    }

    /**
     * Delete a {@link MetadataTemplate} within a {@link Project}
     *
     * @param projectId  {@link Long} identifier for a {@link Project}
     * @param templateId {@link Long} identifier for a {@link MetadataTemplate}
     * @return {@link String} redirects to project > settings > metadata templates
     */
    @RequestMapping(value = "/delete/{templateId}", method = RequestMethod.POST)
    public String deleteMetadataTemplate(@PathVariable Long projectId, @PathVariable Long templateId) {
        Project project = projectService.read(projectId);
        metadataTemplateService.deleteMetadataTemplateFromProject(project, templateId);
        return "redirect:/projects/" + projectId + "/settings/metadata-templates";
    }


    // *************************************************************************
    // AJAX METHODS                                                            *
    // *************************************************************************

    /**
     * Search all Metadata keys available for adding to a template.
     *
     * @param query the query to search for
     * @return a list of keys matching the query
     */
    @RequestMapping("/fields")
    @ResponseBody
    public List<String> getMetadataKeysForProject(@RequestParam(value = "q") String query) {
        return metadataTemplateService.getAllMetadataFieldsByQueryString(query).stream()
                .map(MetadataTemplateField::getLabel).collect(Collectors.toList());
    }
}
