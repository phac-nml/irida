package ca.corefacility.bioinformatics.irida.ria.web.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Admin Panel Page Controller
 *
 */
@Controller
@RequestMapping("/admin")
public class AdminBaseController {

	/**
	 * Get the index page
	 * @return name of the admin panel view
	 */
	@RequestMapping(value = {"", "/**"})
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String getAdminDashboard() {
		return "admin/index";
	}
}
