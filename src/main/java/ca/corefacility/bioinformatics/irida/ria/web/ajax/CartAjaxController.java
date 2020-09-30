package ca.corefacility.bioinformatics.irida.ria.web.ajax;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.AddToCartRequest;
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
	public ResponseEntity<Integer> addSamplesToCart(@RequestBody AddToCartRequest request, Locale locale) {
		return ResponseEntity.ok(service.addSamplesToCart(request));
	}

	@GetMapping("/count")
	public ResponseEntity<Integer> getNumberOfSamplesInCart() {
		return ResponseEntity.ok(service.getNumberOfSamplesInCart());
	}
}
