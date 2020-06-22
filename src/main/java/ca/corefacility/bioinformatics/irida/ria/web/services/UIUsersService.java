package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.context.MessageSource;
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
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.UserSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.AdminUsersTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserTableModel;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableMap;

/**
 * Handles service call for the the administration of the IRIDA users.
 */
@Component
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
public class UIUsersService {
	private final UserService userService;
	private final MessageSource messageSource;

	public UIUsersService(UserService userService, MessageSource messageSource) {
		this.userService = userService;
		this.messageSource = messageSource;
	}

	/**
	 * Get a paged listing of users for the administration user.  This can be filtered and sorted.
	 *
	 * @param request - the information about the current page of users to return
	 * @return {@link TableResponse}
	 */
	public TableResponse<UserTableModel> getUsersPagedList(AdminUsersTableRequest request) {
		Specification<User> specification = UserSpecification.searchUser(request.getSearch());
		PageRequest pageRequest = PageRequest.of(request.getCurrent(), request.getPageSize(), request.getSort());
		Page<User> userPage = userService.search(specification, pageRequest);

		List<UserTableModel> users = userPage.getContent()
				.stream()
				.map(UserTableModel::new)
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
}
