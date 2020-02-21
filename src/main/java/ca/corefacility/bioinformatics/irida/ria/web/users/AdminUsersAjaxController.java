package ca.corefacility.bioinformatics.irida.ria.web.users;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.UserSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.AdminUsersTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserTableModel;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableMap;

@RestController
@RequestMapping("/ajax/users")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminUsersAjaxController {
	private final UserService userService;

	@Autowired
	public AdminUsersAjaxController(UserService userService) {
		this.userService = userService;
	}

	@RequestMapping("/list")
	public TableResponse<UserTableModel> getUsersPagedList(@RequestBody AdminUsersTableRequest request) {
		Specification<User> specification = UserSpecification.searchUser(request.getSearch());
		PageRequest pageRequest = PageRequest.of(request.getCurrent(), request.getPageSize(), request.getSort());
		Page<User> userPage = userService.search(specification, pageRequest);

		List<UserTableModel> users = userPage.getContent()
				.stream()
				.map(UserTableModel::new)
				.collect(Collectors.toList());

		return new TableResponse<>(users, userPage.getTotalElements());
	}

	@RequestMapping("/edit")
	public void updateUserStatus(@RequestParam Long id, @RequestParam boolean isEnabled) {
		User user = userService.read(id);
		if (user.isEnabled() != isEnabled) {
			userService.updateFields(id, ImmutableMap.of("enabled", isEnabled));
		}
	}
}
