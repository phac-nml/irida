package ca.corefacility.bioinformatics.irida.ria.web;

import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * Controller for interacting with {@link UserGroup}.
 */
@Controller
@RequestMapping(value = "/groups")
public class UserGroupsController {

	private static final Logger logger = LoggerFactory.getLogger(UserGroupsController.class);
	private static final String GROUPS_LIST = "groups/list";
	private static final String GROUPS_CREATE = "groups/create";
	private static final String GROUPS_EDIT = "groups/edit";
	private static final String GROUP_DETAILS = "groups/details";
	private static final String GROUPS_USER_MODAL = "groups/remove-user-modal";

	private final UserGroupService userGroupService;
	private final UserService userService;
	private final MessageSource messageSource;

	/**
	 * Create a new groups controller.
	 * 
	 * @param userGroupService
	 *            the {@link UserGroupService}.
	 * @param userService
	 *            the {@link UserService}.
	 * @param messageSource
	 *            the {@link MessageSource}.
	 */
	@Autowired
	public UserGroupsController(final UserGroupService userGroupService, final UserService userService,
			final MessageSource messageSource) {
		this.userGroupService = userGroupService;
		this.messageSource = messageSource;
		this.userService = userService;
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

	@RequestMapping(value = "/{groupId}/delete", method = RequestMethod.POST)
	public String deleteUserGroup(@PathVariable Long groupId) {
		userGroupService.delete(groupId);
		return "redirect:/groups";
	}

	/**
	 * Get the page to create a new group.
	 *
	 * @return the route to the creation page.
	 */
	@RequestMapping("/create")
	public String getCreatePage() {
		return GROUPS_CREATE;
	}

	/**
	 * Create a new {@link UserGroup}.
	 * 
	 * @param userGroup
	 *            the {@link UserGroup} from the request.
	 * @param model
	 *            the model to add violation constraints to.
	 * @param locale
	 *            the locale used by the browser.
	 * @param principal
	 *            The logged in user
	 * @return the route back to the creation page on validation failure, or the
	 *         destails page on success.
	 */
	@RequestMapping(path = "/create", method = RequestMethod.POST)
	public String createGroup(final @ModelAttribute UserGroup userGroup, final Model model, final Locale locale, final Principal principal) {
		logger.debug("Creating group: [ " + userGroup + "]");
		final Map<String, String> errors = new HashMap<>();

		try {
			userGroupService.create(userGroup);
			return "redirect:/groups/" + userGroup.getId();
		} catch (final ConstraintViolationException e) {
			for (final ConstraintViolation<?> v : e.getConstraintViolations()) {
				errors.put(v.getPropertyPath().toString(), v.getMessage());
			}
		} catch (final EntityExistsException | DataIntegrityViolationException e) {
			errors.put("name", messageSource.getMessage("group.name.exists", null, locale));
		}

		model.addAttribute("errors", errors);
		model.addAttribute("given_name", userGroup.getName());
		model.addAttribute("given_description", userGroup.getDescription());

		return GROUPS_CREATE;
	}
}
