package ca.corefacility.bioinformatics.irida.ria.web.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger logger = LoggerFactory.getLogger(AdminBaseController.class);

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
