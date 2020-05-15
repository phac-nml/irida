package ca.corefacility.bioinformatics.irida.ria.web.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminBaseController {

	@RequestMapping("")
	public String getAdminDashboard() {
		return "admin/index";
	}
}
