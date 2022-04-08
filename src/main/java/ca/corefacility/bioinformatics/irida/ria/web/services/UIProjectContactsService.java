package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectContactTableModel;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * Service class for the UI for handling project members actions.
 */
@Component
public class UIProjectContactsService {
	private final ProjectService projectService;
	private final UserService userService;
	private final MessageSource messageSource;

	@Autowired
	public UIProjectContactsService(ProjectService projectService, UserService userService, MessageSource messageSource) {
		this.projectService = projectService;
		this.userService = userService;
		this.messageSource = messageSource;
	}


	/**
	 * Get a paged listing of project contacts passed on parameters set in the table request.
	 *
	 * @param projectId    - identifier for the current project
	 * @param tableRequest - details about the current page of the table
	 * @return sorted and filtered list of project contacts
	 */
	public TableResponse<ProjectContactTableModel> getProjectContacts(Long projectId, TableRequest tableRequest) {
		Project project = projectService.read(projectId);
		Page<Join<Project, User>> contactsForProject = userService.searchUsersForProject(project, tableRequest.getSearch(),
				tableRequest.getCurrent(), tableRequest.getPageSize(), tableRequest.getSort());
		List<ProjectContactTableModel> contacts = contactsForProject.get()
				.map(join -> {
					ProjectUserJoin projectUserJoin = (ProjectUserJoin) join;
					return new ProjectContactTableModel(join.getObject(), join.getObject().getEmail(), projectUserJoin.getCreatedDate());
				})
				.collect(Collectors.toList());
		return new TableResponse<>(contacts, contactsForProject.getTotalElements());
	}

}
