package ca.corefacility.bioinformatics.irida.ria.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to handle entry points to new IRIDA SPAs
 */
@Controller
@RequestMapping("/beta")
public class SPAController {

	/**
	 * Get entry point for the administration panel
	 *
	 * @return Path to template
	 */
	@RequestMapping("/admin/**")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String getAdminPage() {
		return "beta/admin";
	}
}
