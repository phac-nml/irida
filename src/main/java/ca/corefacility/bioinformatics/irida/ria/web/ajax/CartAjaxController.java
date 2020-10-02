package ca.corefacility.bioinformatics.irida.ria.web.ajax;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.cart.CartProject;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.AddToCartRequest;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.RemoveSampleRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.UICartService;

@RestController
@RequestMapping("/ajax/cart")
public class CartAjaxController {
	private final UICartService service;

	@Autowired
	public CartAjaxController(UICartService service) {
		this.service = service;
	}

	@PostMapping("")
	public ResponseEntity<Integer> addSamplesToCart(@RequestBody AddToCartRequest request) {
		return ResponseEntity.ok(service.addSamplesToCart(request));
	}

	@GetMapping("/count")
	public ResponseEntity<Integer> getNumberOfSamplesInCart() {
		return ResponseEntity.ok(service.getNumberOfSamplesInCart());
	}

	@DeleteMapping("/sample")
	public ResponseEntity<Integer> removeSample(@RequestBody RemoveSampleRequest request) {
		return ResponseEntity.ok(service.removeSample(request));
	}

	@DeleteMapping("/project")
	public ResponseEntity<Integer> removeProject(@RequestParam Long id) {
		return ResponseEntity.ok(service.removeProject(id));
	}

	@DeleteMapping("")
	public void emptyCart() {
		service.emptyCart();
	}

	@GetMapping("/ids")
	public ResponseEntity<Set<Long>> getProjectIdsInCart() {
		return ResponseEntity.ok(service.getProjectIdsInCart());
	}

	@GetMapping("/samples")
	public ResponseEntity<List<CartProject>> getCartSamplesForProjects(@RequestParam List<Long> ids) {
		return ResponseEntity.ok(service.getSamplesForProjects(ids));
	}
}
