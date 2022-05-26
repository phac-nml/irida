package ca.corefacility.bioinformatics.irida.ria.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/beta")
public class SPAController {

	@RequestMapping("/admin/**")
	public String getAdminPage() {
		return "beta/admin";
	}
}
