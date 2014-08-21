package ca.corefacility.bioinformatics.irida.model.workflow.manager.galaxy;

import java.net.URL;

import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.workflow.manager.ExecutionManager;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader.DataStorage;

/**
 * Defines an ExecutionManager Galaxy implementation for interacting with Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class ExecutionManagerGalaxy implements ExecutionManager {

	private URL location;
	private String apiKey;
	private GalaxyAccountEmail accountEmail;
	private Uploader.DataStorage dataStorage;
	
	/**
	 * Builds a new ExecutionManagerGalaxy with the given information.
	 * @param location  The location to Galaxy.
	 * @param adminAPIKey  An Admin API key for Galaxy.
	 * @param accountEmail An email for the admin account in Galaxy.
	 * @param dataStorage The data storage mode for Galaxy.
	 */
	public ExecutionManagerGalaxy(URL location, String adminAPIKey,
			GalaxyAccountEmail accountEmail, DataStorage dataStorage) {
		this.location = location;
		this.apiKey = adminAPIKey;
		this.accountEmail = accountEmail;
		this.dataStorage = dataStorage;
	}

	public URL getLocation() {
		return location;
	}

	public String getAdminAPIKey() {
		return apiKey;
	}

	public GalaxyAccountEmail getAccountEmail() {
		return accountEmail;
	}

	public Uploader.DataStorage getDataStorage() {
		return dataStorage;
	}
}
