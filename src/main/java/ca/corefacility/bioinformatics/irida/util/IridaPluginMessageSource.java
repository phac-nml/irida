package ca.corefacility.bioinformatics.irida.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * A {@link MessageSource} used to store and search through
 * {@link MessageSource}s for IRIDA Plugins.
 *
 */
public class IridaPluginMessageSource implements HierarchicalMessageSource {

	private static final Logger logger = LoggerFactory.getLogger(IridaPluginMessageSource.class);

	private List<MessageSource> pluginSources;
	private MessageSource parent = null;

	/**
	 * Builds a new {@link IridaPluginMessageSource} which makes use of the given
	 * sources.
	 * 
	 * @param pluginSources A list of {@link MessageSource}s to use for the plugin message source.
	 */
	public IridaPluginMessageSource(List<MessageSource> pluginSources) {
		checkNotNull(pluginSources, "pluginSources is null");
		checkArgument(pluginSources.size() > 0, "pluginSources is empty");

		this.pluginSources = pluginSources;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		try {
			String message = getMessage(code, args, locale);

			if (message == null) {
				return defaultMessage;
			} else {
				return message;
			}
		} catch (NoSuchMessageException e) {
			return defaultMessage;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
		return getMessage(new DefaultMessageSourceResolvable(new String[] { code }, args), locale);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
		checkNotNull(resolvable, "resolvable is null");

		String message = null;

		for (MessageSource messageSource : pluginSources) {
			try {
				message = messageSource.getMessage(resolvable, locale);
				if (message != null) {
					break;
				}
			} catch (NoSuchMessageException e) {
				logger.trace("Got NoSuchMessageException for " + messageSource, e);
			}
		}

		if (message == null) {
			if (parent == null) {
				throw new NoSuchMessageException("Could not find message for '" + resolvable + "'");
			} else {
				return parent.getMessage(resolvable, locale);
			}
		}

		return message;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setParentMessageSource(MessageSource parent) {
		checkNotNull(parent, "parent is null");

		this.parent = parent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MessageSource getParentMessageSource() {
		return parent;
	}

}
