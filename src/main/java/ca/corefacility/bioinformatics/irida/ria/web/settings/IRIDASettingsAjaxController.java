package ca.corefacility.bioinformatics.irida.ria.web.settings;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.ria.web.services.UISettingsService;
import ca.corefacility.bioinformatics.irida.ria.web.settings.dto.Role;

/**
 * Handles asynchronous requests for IRIDA settings.
 */
@RestController
@RequestMapping("/ajax/settings")
public class IRIDASettingsAjaxController {

	private final UISettingsService service;

	@Autowired
	public IRIDASettingsAjaxController(UISettingsService service) {
		this.service = service;
	}

	/**
	 * Get a list of all locales.
	 *
	 * @return list of locales
	 */
	@RequestMapping("/locales")
	public ResponseEntity<List<ca.corefacility.bioinformatics.irida.ria.web.settings.dto.Locale>> getLocales() {
		return ResponseEntity.ok(service.getLocales());
	}

	/**
	 * Get a list of all system roles.
	 *
	 * @param locale - {@link Locale} of the current user
	 * @return list of system roles
	 */
	@RequestMapping("/roles")
	public ResponseEntity<List<Role>> getSystemRoles(Locale locale) {
		return ResponseEntity.ok(service.getSystemRoles(locale));
	}

	/**
	 * Get if email is configured.
	 *
	 * @return if email is configured
	 */
	@RequestMapping("/emailConfigured")
	public ResponseEntity<Boolean> getEmailConfigured() {
		return ResponseEntity.ok(service.getEmailConfigured());
	}

}
