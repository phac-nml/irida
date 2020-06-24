package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.NcbiExportSubmissionTableModel;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.export.NcbiExportSubmissionService;

@Component
public class UINcbiService {
	private final ProjectService projectService;
	private final NcbiExportSubmissionService ncbiService;

	@Autowired
	public UINcbiService(ProjectService projectService, NcbiExportSubmissionService ncbiService) {
		this.projectService = projectService;
		this.ncbiService = ncbiService;
	}

	public List<NcbiExportSubmissionTableModel> getNCBIExportsForProject(Long projectId) {
		Project project = projectService.read(projectId);
		List<NcbiExportSubmission> submissions = ncbiService.getSubmissionsForProject(project);
		return submissions.stream().map(NcbiExportSubmissionTableModel::new).collect(Collectors.toList());
	}
}
