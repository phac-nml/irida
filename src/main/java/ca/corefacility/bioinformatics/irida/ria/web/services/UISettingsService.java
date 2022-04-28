package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.model.user.Role;

import com.google.common.collect.ImmutableList;

/**
 * Handles service calls for settings in IRIDA.
 */
@Component
public class UISettingsService {
	private final MessageSource messageSource;
	private final List<Locale> locales;
	private final List<Role> SYSTEM_ROLES = ImmutableList.of(Role.ROLE_ADMIN, Role.ROLE_MANAGER, Role.ROLE_USER,
			Role.ROLE_TECHNICIAN, Role.ROLE_SEQUENCER);

	public UISettingsService(IridaApiServicesConfig.IridaLocaleList locales, MessageSource messageSource) {
		this.messageSource = messageSource;
		this.locales = locales.getLocales();
	}

	/**
	 * Get a list of all locales.
	 *
	 * @return list of locales.
	 */
	public List<ca.corefacility.bioinformatics.irida.ria.web.settings.dto.Locale> getLocales() {
		return locales.stream()
				.map(locale -> new ca.corefacility.bioinformatics.irida.ria.web.settings.dto.Locale(
						locale.getLanguage(), locale.getDisplayName()))
				.collect(Collectors.toList());
	}

	/**
	 * Get a list of all system roles.
	 *
	 * @param locale - {@link Locale}
	 * @return list of system roles.
	 */
	public List<ca.corefacility.bioinformatics.irida.ria.web.settings.dto.Role> getSystemRoles(Locale locale) {
		return SYSTEM_ROLES.stream()
				.map(role -> new ca.corefacility.bioinformatics.irida.ria.web.settings.dto.Role(role.getName(),
						messageSource.getMessage("systemRole." + role.getName(), new Object[] {}, locale)))
				.collect(Collectors.toList());
	}
}
