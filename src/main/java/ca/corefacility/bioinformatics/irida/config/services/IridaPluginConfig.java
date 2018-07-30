package ca.corefacility.bioinformatics.irida.config.services;

import ca.corefacility.bioinformatics.irida.plugins.IridaPlugin;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

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
	 * @return a {@link IridaPluginList} containing all the loaded {@link IridaPlugin}
	 */
	@Bean(name = "iridaPipelinePlugins")
	public IridaPluginList iridaPipelinePlugins() {
		PluginManager pluginManager = new DefaultPluginManager(PIPELINE_PLUGIN_PATH);

		//TODO: Add exception management here to more gracefully handle failed pipeline loads
		pluginManager.loadPlugins();
		pluginManager.startPlugins();

		List<IridaPlugin> extensions = pluginManager.getExtensions(IridaPlugin.class);

		logger.debug("Loaded " + extensions.size() + " pipeline plugins.");

		return new IridaPluginList(extensions);
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
