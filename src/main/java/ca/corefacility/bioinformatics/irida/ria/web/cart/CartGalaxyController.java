package ca.corefacility.bioinformatics.irida.ria.web.cart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.cart.components.Cart;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.CartSample;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.GalaxyExportAuthentication;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.GalaxyExportSample;
import ca.corefacility.bioinformatics.irida.service.IridaClientDetailsService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

@RestController
@Scope("session")
@RequestMapping("/ajax/galaxy-export")
public class CartGalaxyController {
	private IridaClientDetailsService clientDetailsService;
	private SampleService sampleService;
	private Cart cart;

	@Autowired
	public CartGalaxyController(IridaClientDetailsService clientDetailsService, SampleService sampleService,
			Cart cart) {
		this.clientDetailsService = clientDetailsService;
		this.sampleService = sampleService;
		this.cart = cart;
	}

	/**
	 * This is a check to see if the is currently an authentication token for the Galaxy client.
	 *
	 * @param clientId {@link String} The clientId for the Galaxy client
	 * @return {@link GalaxyExportAuthentication} which contains the status for the client.
	 */
	@RequestMapping("/authorized")
	public GalaxyExportAuthentication isGalaxyClientAuthenticated(@RequestParam String clientId) {
		IridaClientDetails details = (IridaClientDetails) clientDetailsService.loadClientByClientId(clientId);
		int activeTokensForClient = clientDetailsService.countActiveTokensForClient(details);
		return new GalaxyExportAuthentication(0 < activeTokensForClient);
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
}
