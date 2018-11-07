package ca.corefacility.bioinformatics.irida.config.services;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.pf4j.DefaultPluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.plugins.IridaPlugin;
import ca.corefacility.bioinformatics.irida.plugins.IridaPluginException;

/**
 * Configuration file for loading IRIDA plugins
 */
@Configuration
public class IridaPluginConfig {

	private static final Logger logger = LoggerFactory.getLogger(IridaPluginConfig.class);

	private static final Path PIPELINE_PLUGIN_PATH = Paths.get("/etc/irida/plugins");

	/**
	 * Get the list of IRIDA pipeline plugins
	 *
	 * @return a {@link IridaPluginList} containing all the loaded
	 *         {@link IridaPlugin}
	 */
	@Bean(name = "iridaPipelinePlugins")
	public IridaPluginList iridaPipelinePlugins() {
		DefaultPluginManager pluginManager = new DefaultPluginManager(PIPELINE_PLUGIN_PATH);
		pluginManager.setSystemVersion(IridaPlugin.PLUGIN_API_VERSION);
		pluginManager.setExactVersionAllowed(true);

		pluginManager.loadPlugins();
		pluginManager.startPlugins();

		List<IridaPlugin> extensions = getValidPlugins(pluginManager.getExtensions(IridaPlugin.class));

		logger.debug("Loaded " + extensions.size() + " valid pipeline plugins.");

		return new IridaPluginList(extensions);
	}

	/**
	 * Filters the passed list of {@link IridaPlugin}s to only include those plugins
	 * that are properly defined.
	 * 
	 * @param plugins The initial list of {@link IridaPlugin}s to search through.
	 * @return A list of {@link IridaPlugin}s that properly implement
	 *         {@link IridaPlugin}.
	 */
	private List<IridaPlugin> getValidPlugins(List<IridaPlugin> plugins) {
		List<IridaPlugin> validPlugins = Lists.newArrayList();

		// for each plugin, verify it properly implements all required methods
		for (IridaPlugin plugin : plugins) {
			try {
				if (plugin.getAnalysisType() != null && plugin.getDefaultWorkflowUUID() != null
						&& plugin.getUpdater(null, null, null) != null && plugin.getWorkflowsPath() != null
						&& plugin.getBackgroundColor() != null && plugin.getTextColor() != null) {
					logger.trace("Irida plugin [" + plugin.getClass() + "] is valid");

					validPlugins.add(plugin);
				} else {
					logger.error("Plugin [" + plugin.getClass()
							+ "] does not properly implement all required methods. Disabling the plugin.");
				}
			} catch (IridaPluginException | AbstractMethodError e) {
				logger.error("Error validating plugin [" + plugin.getClass() + "]. Disabling the plugin.", e);
			}
		}

		return validPlugins;
	}

	/**
	 * Class containing the list of all loaded {@link IridaPlugin}
	 */
	public class IridaPluginList {
		private List<IridaPlugin> plugins;

		public IridaPluginList(List<IridaPlugin> plugins) {
			this.plugins = plugins;
		}

		public List<IridaPlugin> getPlugins() {
			return plugins;
		}
	}

}
