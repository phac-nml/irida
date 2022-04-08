package ca.corefacility.bioinformatics.irida.ria.web.ajax.projects;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectContactTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectContactsService;

/**
 * Controller for all asynchronous request from the UI for Project Contacts
 */
@RestController
@RequestMapping("/ajax/projects/{projectId}/contacts")
public class ProjectContactsAjaxController {
	private final UIProjectContactsService projectContactsService;

	@Autowired
	public ProjectContactsAjaxController(UIProjectContactsService projectContactsService) {
		this.projectContactsService = projectContactsService;
	}

	/**
	 * Get a paged listing of project contacts passed on parameters set in the table request.
	 *
	 * @param projectId    - identifier for the current project
	 * @param tableRequest - details about the current page of the table
	 * @return sorted and filtered list of project contacts
	 */
	@RequestMapping("")
	public ResponseEntity<TableResponse<ProjectContactTableModel>> getProjectContacts(@PathVariable Long projectId,
			@RequestBody TableRequest tableRequest) {
		return ResponseEntity.ok(projectContactsService.getProjectContacts(projectId, tableRequest));
	}

}
