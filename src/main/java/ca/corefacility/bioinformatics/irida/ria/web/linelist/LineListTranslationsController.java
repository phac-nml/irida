package ca.corefacility.bioinformatics.irida.ria.web.linelist;

import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;

/**
 * Controller to handle translations on the Line List Page.
 * This is a here since individual components can be added to the page dynamically during
 * runtime.  Since a lot of features will not be loaded initially and might never be used
 * there was no need to load their translations directly onto the page.
 */
@RestController
@RequestMapping("/linelist/translations")
public class LineListTranslationsController {
	private MessageSource messageSource;

	@Autowired
	public LineListTranslationsController(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * This is for general translations on the line list page.
	 *
	 * @param locale {@link Locale}
	 * @return {@link Map} of translations
	 */
	@RequestMapping
	public Map<String, String> getLineListBaseTranslations(Locale locale) {
		// @formatter:off
		return ImmutableMap.of(
		);
		// @formatter:on
	}

	/**
	 * Handle translation for the Metadata Field Menu in the Table Headers.
	 *
	 * @param locale {@link Locale}
	 * @return {@link Map} of translations
	 */
	@RequestMapping("MetadataFieldMenu")
	public Map<String, String> getMetadataFieldMenuTranslations(Locale locale) {
		// @formatter:off
		return ImmutableMap.of(
		"MetadataFieldMenu_remove_entries",
				messageSource.getMessage("MetadataFieldMenu_remove_entries", new Object[] {}, locale)
		);
		// @formatter:on
	}

	/**
	 * This is to handle translations for the Remove Metadata Template Field modal.  This component is lazy
	 * loaded so the messages need to be separate.
	 *
	 * @param locale {@link Locale}
	 * @return {@link Map} of translations
	 */
	@RequestMapping("/RemoveMetadataEntriesModal")
	public Map<String, String> getRemoveMetadataEntriesModalTranslations(Locale locale) {
		// @formatter:off
		return ImmutableMap.of(
		"RemoveMetadataEntriesModal_title",
				messageSource.getMessage("RemoveMetadataEntriesModal_title", new Object[] {}, locale),
		"RemoveMetadataEntriesModal_intro",
				messageSource.getMessage("RemoveMetadataEntriesModal_intro", new Object[] {}, locale),
		"RemoveMetadataEntriesModal_warning",
				messageSource.getMessage("RemoveMetadataEntriesModal_warning", new Object[] {}, locale),
		"RemoveMetadataEntriesModal_confirm",
				messageSource.getMessage("RemoveMetadataEntriesModal_confirm",new Object[]{}, locale)
		);
		// @formatter:on
	}
}
