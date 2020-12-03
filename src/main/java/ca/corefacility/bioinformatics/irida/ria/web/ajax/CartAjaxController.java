package ca.corefacility.bioinformatics.irida.ria.web.ajax;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.cart.CartProjectModel;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.AddToCartRequest;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.AddToCartResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UICartService;

/**
 * AJAX controller for cart functionality
 */
@RestController
@RequestMapping("/ajax/cart")
public class CartAjaxController {
	private final UICartService service;

	@Autowired
	public CartAjaxController(UICartService service) {
		this.service = service;
	}

	/**
	 * Add a set of samples to the cart from a particular project
	 *
	 * @param request Request to add sample to the cart
	 * @param locale Currently logged in users set locale
	 * @return the number of samples currently in the cart
	 */
	@PostMapping("")
	public ResponseEntity<AddToCartResponse> addSamplesToCart(@RequestBody AddToCartRequest request, Locale locale) {
		return ResponseEntity.ok(service.addSamplesToCart(request, locale));
	}

	/**
	 * Get the number of samples from all projects that are currently in the cart
	 *
	 * @return the number of samples currently in the cart
	 */
	@GetMapping("/count")
	public ResponseEntity<Integer> getNumberOfSamplesInCart() {
		return ResponseEntity.ok(service.getNumberOfSamplesInCart());
	}

	/**
	 * Remove a sample from the cart
	 *
	 * @param request Request to move a sample from the cart
	 * @return the number of samples currently in the cart
	 */
	@DeleteMapping("/sample/{sampleId}")
	public ResponseEntity<Integer> removeSample(@PathVariable Long sampleId) {
		return ResponseEntity.ok(service.removeSample(sampleId));
	}

	/**
	 * Remove all samples from a specific project from the cart
	 *
	 * @param id for a project
	 * @return the number of samples currently in the cart
	 */
	@DeleteMapping("/project")
	public ResponseEntity<Integer> removeProject(@RequestParam Long id) {
		return ResponseEntity.ok(service.removeProject(id));
	}

	/**
	 * Completely empty the cart
	 */
	@DeleteMapping("")
	public void emptyCart() {
		service.emptyCart();
	}

	/**
	 * Get a list of project identifiers for projects that have samples in the cart.
	 *
	 * @return list of project identifiers
	 */
	@GetMapping("/ids")
	public ResponseEntity<Set<Long>> getProjectIdsInCart() {
		return ResponseEntity.ok(service.getProjectIdsInCart());
	}

	/**
	 * Get the samples that are in the cart for a specific project
	 *
	 * @param ids List of project identifiers to get the samples for.
	 * @return Samples that are currently in the cart for specific projects
	 */
	@GetMapping("/samples")
	public ResponseEntity<List<CartProjectModel>> getCartSamplesForProjects(@RequestParam List<Long> ids) {
		return ResponseEntity.ok(service.getSamplesForProjects(ids));
	}
}
