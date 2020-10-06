package ca.corefacility.bioinformatics.irida.ria.web.cart;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.config.GalaxySessionInterceptor;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.GalaxyExportSample;
import ca.corefacility.bioinformatics.irida.ria.web.services.UICartService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Controller to handle all ajax requests made to the cart that have to do with Galaxy.
 */
@RestController
@RequestMapping("/ajax/galaxy-export")
public class CartGalaxyController {
	private final SampleService sampleService;
	private final UICartService cartService;

	@Autowired
	public CartGalaxyController(SampleService sampleService, UICartService cartService) {
		this.sampleService = sampleService;
		this.cartService = cartService;
	}

	/**
	 * Get a list of links for all {@link Sample} to be exported to the Galaxy Client.
	 * @return {@link List} of {@link Sample} links.
	 */
	@RequestMapping("/samples")
	public List<GalaxyExportSample> getGalaxyExportForm() {
		Map<Project, List<Sample>> contents = cartService.getFullCart();
		return contents.entrySet().stream().map(entry -> entry.getValue().stream().map(sample -> new GalaxyExportSample(sample, entry.getKey().getId()))).flatMap(
				Stream::distinct).collect(Collectors.toList());
	}

	/**
	 * Remove the Galaxy attributes from the session.
	 *
	 * @param request - the current {@link HttpServletRequest}
	 */
	@RequestMapping("remove")
	public void removeGalaxySession(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.removeAttribute(GalaxySessionInterceptor.GALAXY_CALLBACK_URL);
		session.removeAttribute(GalaxySessionInterceptor.GALAXY_CLIENT_ID);
	}
}
