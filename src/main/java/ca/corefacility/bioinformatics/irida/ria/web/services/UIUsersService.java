package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.UserSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.PasswordResetController;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.AdminUsersTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserDetailsModel;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserDetailsResponse;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableMap;

/**
 * Handles service call for the the administration of the IRIDA users.
 */
@Component
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
public class UIUsersService {
	private final UserService userService;
	private final ProjectService projectService;
	private final EmailController emailController;
	private final MessageSource messageSource;

	public UIUsersService(UserService userService, ProjectService projectService, EmailController emailController,
			MessageSource messageSource) {
		this.userService = userService;
		this.projectService = projectService;
		this.emailController = emailController;
		this.messageSource = messageSource;
	}

	/**
	 * Get a paged listing of users for the administration user.  This can be filtered and sorted.
	 *
	 * @param request - the information about the current page of users to return
	 * @return {@link TableResponse}
	 */
	public TableResponse<UserDetailsModel> getUsersPagedList(AdminUsersTableRequest request) {
		Specification<User> specification = UserSpecification.searchUser(request.getSearch());
		PageRequest pageRequest = PageRequest.of(request.getCurrent(), request.getPageSize(), request.getSort());
		Page<User> userPage = userService.search(specification, pageRequest);

		List<UserDetailsModel> users = userPage.getContent()
				.stream()
				.map(UserDetailsModel::new)
				.collect(Collectors.toList());

		return new TableResponse<>(users, userPage.getTotalElements());
	}

	/**
	 * Update a user status (if the user is enabled within IRIDA).
	 *
	 * @param id        - identifier for an {@link User}
	 * @param isEnabled - whether the user should be enabled.
	 * @param locale    - users {@link Locale}
	 * @return {@link ResponseEntity}
	 */
	public ResponseEntity<String> updateUserStatus(Long id, boolean isEnabled, Locale locale) {
		User user = userService.read(id);
		if (user.isEnabled() != isEnabled) {
			try {
				userService.updateFields(id, ImmutableMap.of("enabled", isEnabled));
				String key = isEnabled ? "server.AdminUsersService.enabled" : "server.AdminUsersService.disabled";
				return ResponseEntity.status(HttpStatus.OK)
						.body(messageSource.getMessage(key, new Object[] { user.getUsername() }, locale));
			} catch (EntityExistsException | EntityNotFoundException | ConstraintViolationException | InvalidPropertyException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(messageSource.getMessage("server.AdminUsersService.error",
								new Object[] { user.getUsername() }, locale));
			}

		}
		// Should never hit here!
		return ResponseEntity.status(HttpStatus.ALREADY_REPORTED)
				.body("");
	}

	/**
	 * Get the details for a specific user
	 *
	 * @param userId      - the id for the user to show details for
	 * @param mailFailure - if sending a user activation e-mail passed or failed
	 * @param principal   - the currently logged in user
	 * @return user details for a specific user
	 */
	public UserDetailsResponse getUser(Long userId, Boolean mailFailure, Principal principal) {
		UserDetailsResponse response = new UserDetailsResponse();
		User user = userService.read(userId);
		UserDetailsModel userDetails = new UserDetailsModel(user);
		response.setUser(userDetails);
		response.setMailFailure(mailFailure);

		User principalUser = userService.getUserByUsername(principal.getName());

		Locale locale = LocaleContextHolder.getLocale();

		// add the user's role to the model
		String roleMessageName = "systemrole." + user.getSystemRole()
				.getName();
		String systemRole = messageSource.getMessage(roleMessageName, null, locale);
		response.setSystemRole(systemRole);

		// check if we should show an edit button
		boolean canEditUser = canEditUser(principalUser, user);
		response.setCanEditUser(canEditUser);
		response.setMailConfigured(emailController.isMailConfigured());

		response.setCanCreatePasswordReset(PasswordResetController.canCreatePasswordReset(principalUser, user));

		// show the user's projects
		List<Join<Project, User>> projectsForUser = projectService.getProjectsForUser(user);

		// add the projects to the model list
		List<Map<String, Object>> projects = new ArrayList<>();
		for (Join<Project, User> join : projectsForUser) {
			ProjectUserJoin pujoin = (ProjectUserJoin) join;
			Project project = join.getSubject();
			Map<String, Object> map = new HashMap<>();
			map.put("identifier", project.getId());
			map.put("name", project.getName());
			map.put("isManager", pujoin.getProjectRole()
					.equals(ProjectRole.PROJECT_OWNER));
			map.put("subscribed", pujoin.isEmailSubscription());

			String proleMessageName = "projectRole." + pujoin.getProjectRole()
					.toString();
			map.put("role", messageSource.getMessage(proleMessageName, null, locale));
			map.put("date", pujoin.getCreatedDate());
			projects.add(map);
		}
		response.setProjects(projects);
		return response;
	}

	/**
	 * Check if the logged in user is allowed to edit the given user.
	 *
	 * @param principalUser - the currently logged in principal
	 * @param user          - the user to edit
	 * @return boolean if the principal can edit the user
	 */
	private boolean canEditUser(User principalUser, User user) {
		boolean principalAdmin = principalUser.getAuthorities()
				.contains(Role.ROLE_ADMIN);
		boolean usersEqual = user.equals(principalUser);

		return principalAdmin || usersEqual;
	}
}
