package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import java.util.UUID;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.beans.History;

import static com.google.common.base.Preconditions.*;

/**
 * Class for working with Galaxy Histories.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyHistory {
	
	private GalaxyInstance galaxyInstance;
	
	/**
	 * Builds a new GalaxyHistory with the given Galaxy instance.
	 * @param galaxyInstance  The Galaxy Instance to use to connect to Galaxy.
	 */
	public GalaxyHistory(GalaxyInstance galaxyInstance) {
		checkNotNull(galaxyInstance, "galaxyInstance is null");
		
		this.galaxyInstance = galaxyInstance;
	}
	
	public History newHistoryForWorkflow() {
		HistoriesClient historiesClient = galaxyInstance.getHistoriesClient();

		History history = new History();
		history.setName(UUID.randomUUID().toString());
		return historiesClient.create(history);
	}
}
