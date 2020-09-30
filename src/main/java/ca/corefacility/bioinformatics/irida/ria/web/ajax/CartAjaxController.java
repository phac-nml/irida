package ca.corefacility.bioinformatics.irida.ria.web.ajax;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public ResponseEntity<String> addSamplesToCart(@RequestBody AddToCartRequest request, Locale locale) {
		return ResponseEntity.ok("SDFKLJ");
	}
}
