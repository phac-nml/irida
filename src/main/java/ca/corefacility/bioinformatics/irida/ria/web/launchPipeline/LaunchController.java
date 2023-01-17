package ca.corefacility.bioinformatics.irida.ria.web.launchPipeline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.corefacility.bioinformatics.irida.ria.web.services.UICartService;

/**
 * Controller for workflow launch pages.
 */
@Controller
@RequestMapping("/launch")
public class LaunchController {
	private UICartService cartService;

	@Autowired
	public void setCartService(UICartService cartService) {
		this.cartService = cartService;
	}

	/**
	 * Mapping for the pipeline launch page.
	 *
	 * @param projectId used to indicate if this was set for an automated pipeline
	 * @return The path to the launch page html file.
	 */
	@GetMapping("")
	public String getPipelineLaunchPage(@RequestParam(required = false) Long projectId) {
		if (projectId != null || !cartService.isCartEmpty()) {
			return "launch";
		}
		// User cannot launch a pipeline if the cart is empty.
		return "redirect:/cart/pipelines";
	}
}
