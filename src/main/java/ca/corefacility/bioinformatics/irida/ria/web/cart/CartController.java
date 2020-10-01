package ca.corefacility.bioinformatics.irida.ria.web.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.corefacility.bioinformatics.irida.ria.web.oauth.GalaxyRedirectionEndpointController;

/**
 * Controller managing interactions with the selected sequences
 */
@Controller
@RequestMapping("/cart")
public class CartController {
	@Value("${server.base.url}")
	private String serverBaseUrl;
	/*
	 * Additional variables
	 */
	private final String iridaPipelinePluginStyle;

	@Autowired
	public CartController(@Qualifier("iridaPipelinePluginStyle") String iridaPipelinePluginStyle) {
		this.iridaPipelinePluginStyle = iridaPipelinePluginStyle;
	}

	/**
	 * Get the dedicated page for the Cart
	 *
	 * @param model {@link Model}
	 * @param automatedProject The ID of the automated project to add a pipeline to (optional)
	 * @return {@link String} path to the cart page template
	 */
	@RequestMapping(value = {"", "/*"}, produces = MediaType.TEXT_HTML_VALUE)
	public String getCartPage(Model model,
			@RequestParam(required = false, name = "automatedProject")
					Long automatedProject) {
		/*
		 * The cart is dynamically created at runtime using React.
		 * Base file at src/main/webapp/resources/js/pages/cart/components/Cart.jsx
		 */

		String galaxyRedirect = GalaxyRedirectionEndpointController.getGalaxyRedirect(serverBaseUrl);

		model.addAttribute("pipeline_plugin_style", iridaPipelinePluginStyle);
		model.addAttribute("automatedProject", automatedProject);
		model.addAttribute("galaxyRedirect", galaxyRedirect);

		return "cart";
	}
}
