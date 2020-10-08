package ca.corefacility.bioinformatics.irida.ria.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;

/**
 * Controller for interacting with {@link UserGroup}.
 */
@Controller
@RequestMapping(value = "/groups")
public class UserGroupsController {

	private static final String GROUPS_LIST = "groups/groups";

	private final UserGroupService userGroupService;

	/**
	 * Create a new groups controller.
	 *
	 * @param userGroupService the {@link UserGroupService}.
	 */
	@Autowired
	public UserGroupsController(final UserGroupService userGroupService) {
		this.userGroupService = userGroupService;
	}

	/**
	 * Get the default index page for listing groups.
	 *
	 * @return the route to the index page.
	 */
	@RequestMapping("")
	public String getIndex() {
		return GROUPS_LIST;
	}

	/**
	 * Get the default index page for listing groups.
	 *
	 * @param groupId the identifier for the user group
	 * @return the route to the index page.
	 */
	@RequestMapping("/{groupId}")
	public String getGroupDetailsPage(@PathVariable Long groupId) {
		/*
		Try reading the project to make sure it exists first.
		 */
		userGroupService.read(groupId);
		return GROUPS_LIST;
	}
}
