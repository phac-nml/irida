package ca.corefacility.bioinformatics.irida.ria.web.cart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.config.GalaxySessionInterceptor;
import ca.corefacility.bioinformatics.irida.ria.web.cart.components.Cart;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.CartSample;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.GalaxyExportSample;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Controller to handle all ajax requests made to the cart that have to do with Galaxy.
 */
@RestController
@Scope("session")
@RequestMapping("/ajax/galaxy-export")
public class CartGalaxyController {
	private SampleService sampleService;
	private Cart cart;

	@Autowired
	public CartGalaxyController(SampleService sampleService, Cart cart) {
		this.sampleService = sampleService;
		this.cart = cart;
	}

	/**
	 * Get a list of links for all {@link Sample} to be exported to the Galaxy Client.
	 * @return {@link List} of {@link Sample} links.
	 */
	@RequestMapping("/samples")
	public List<GalaxyExportSample> getGalaxyExportForm() {
		Map<Long, Map<Long, CartSample>> contents = cart.get();
		List<GalaxyExportSample> result = new ArrayList<>();
		for (Long projectId : contents.keySet()) {
			Iterable<Sample> samples = sampleService.readMultiple(contents.get(projectId)
					.keySet());
			samples.forEach(s -> result.add(new GalaxyExportSample(s, projectId)));
		}
		return result;
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
