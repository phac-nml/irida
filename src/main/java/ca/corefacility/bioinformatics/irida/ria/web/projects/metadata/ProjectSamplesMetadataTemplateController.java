package ca.corefacility.bioinformatics.irida.ria.web.projects.metadata;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.ria.web.projects.metadata.domain.Template;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

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

    @RequestMapping("/new")
    public String getMetadataTemplateListPage(@PathVariable Long projectId,
                                              Model model,
                                              Principal principal) {
        Project project = projectService.read(projectId);
        model.addAttribute("template", new Template());
        projectControllerUtils.getProjectTemplateDetails(model, principal, project);
        return "projects/project_samples_metadata_template";
    }

    @RequestMapping("/{templateId}")
    public String getMetadataTemplatePage(@PathVariable Long projectId,
                                          @PathVariable Long templateId,
                                          Principal principal,
                                          Model model) {
        Project project = projectService.read(projectId);
        projectControllerUtils.getProjectTemplateDetails(model, principal, project);
        MetadataTemplate metadataTemplate = metadataTemplateService.read(templateId);
        Template template = new Template();
        template.setId(metadataTemplate.getId());
        template.setName(metadataTemplate.getName());
        List<String> fields = new ArrayList<>();
        for (MetadataTemplateField field : metadataTemplate.getFields()) {
            fields.add(field.getLabel());
        }
        template.setFields(fields);
        model.addAttribute("template", template);
        return "projects/project_samples_metadata_template";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveMetadataTemplate(@PathVariable Long projectId, Template template) {
//        MetadataTemplate metadataTemplate;
//        if (template.getId() != null) {
//            metadataTemplate = metadataTemplateService.read(template.getId());
//        }
        return "redirect:/projects/" + projectId + "/metadata-templates/" + template.getId();
    }
}
