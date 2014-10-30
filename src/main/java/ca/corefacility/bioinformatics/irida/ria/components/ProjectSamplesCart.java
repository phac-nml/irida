package ca.corefacility.bioinformatics.irida.ria.components;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ProjectSamplesCart {
	private static final Logger logger = LoggerFactory.getLogger(ProjectSamplesCart.class);
	// project.id >> sample.id >> file.id
	private Map<Long, Map<Long, Map<Long, Boolean>>> _cart;

	/*
	 * SERVICES
	 */
	private SampleService sampleService;
	private SequenceFileService sequenceFileService;

	@Autowired
	public ProjectSamplesCart(SampleService sampleService,
			SequenceFileService sequenceFileService) {
		this.sampleService = sampleService;
		this.sequenceFileService = sequenceFileService;
		_cart = new HashMap<>();
	}

	/**
	 * Add a specific sample to the cart within a project.
	 *
	 * @param projectId
	 * 		the id for a {@link Project}
	 * @param sampleId
	 * 		the id for a {@link Sample}
	 *
	 * @return The number of samples selected in the project.
	 */
	public int addSampleToCart(Long projectId, Long sampleId) {
		if (!_cart.containsKey(projectId)) {
			logger.info("Creating a new cart for project [" + projectId + "]");
			_cart.put(projectId, new HashMap<>());
		}

		// Get the project specific samples map
		Map<Long, Map<Long, Boolean>> samplesMap = _cart.get(projectId);

		if (samplesMap.containsKey(sampleId)) {
			selectAllFilesInSample(samplesMap.get(sampleId));
		} else {
			samplesMap.put(sampleId, generateFileMap(sampleId, true));
		}
		return samplesMap.size();
	}

	/**
	 * Remove a specific sample to the cart within a project.
	 *
	 * @param projectId
	 * 		the id for a {@link Project}
	 * @param sampleId
	 * 		the id for a {@link Sample}
	 *
	 * @return The number of samples selected in the project.
	 */
	public int removeSampleFromCart(Long projectId, Long sampleId) {
		if (!_cart.containsKey(projectId)) {
			logger.error("Trying to remove a sample from a project not selected");
			return 0;
		}

		// Get the project specific samples map
		Map<Long, Map<Long, Boolean>> samplesMap = _cart.get(projectId);
		samplesMap.remove(sampleId);
		return samplesMap.size();
	}

	/**
	 * Determine if a sample is in the cart for a project.
	 *
	 * @param projectId
	 * 		the id for a {@link Project}
	 * @param sampleId
	 * 		the id for a {@link Sample}
	 *
	 * @return True if the sample is in the cart.
	 */
	public boolean isSampleInCart(Long projectId, Long sampleId) {
		if (!_cart.containsKey(projectId)) {
			return false;
		}
		Map<Long, Map<Long, Boolean>> samplesMap = _cart.get(projectId);
		return samplesMap.containsKey(sampleId);
	}

	/**
	 * Determine if a file in a sample if a project is in the cart
	 *
	 * @param projectId
	 * 		the id for a {@link Project}
	 * @param sampleId
	 * 		the id for a {@link Sample}
	 *
	 * @return True if the file is active
	 */
	public boolean isFileInCart(Long projectId, Long sampleId, Long fileId) {
		if (!_cart.containsKey(projectId)) {
			return false;
		}
		Map<Long, Map<Long, Boolean>> sampleMap = _cart.get(projectId);
		if (!sampleMap.containsKey(sampleId)) {
			return false;
		}
		return sampleMap.get(sampleId).get(fileId);
	}

	/**
	 * Get a list of all the samples in the caret
	 *
	 * @param projectId
	 * 		the id for a {@link Project}
	 *
	 * @return List of all samples in the cart for the project.
	 */
	public Set<Long> getSelectedSamples(Long projectId) {
		if (_cart.containsKey(projectId)) {
			return _cart.get(projectId).keySet();
		}
		return new HashSet<>();
	}

	/**
	 * Add a file to the cart.  If the project or sample are not in the cart, add them
	 *
	 * @param projectId
	 * 		Id for the {@link Project}
	 * @param sampleId
	 * 		Id for the {@link Sample}
	 * @param fileId
	 * 		Id for the {@link SequenceFile}
	 *
	 * @return The size of the cart for that project
	 */
	public int addFileToCart(Long projectId, Long sampleId, Long fileId) {
		Map<Long, Map<Long, Boolean>> projectMap = _cart.get(projectId);
		if (projectMap == null) {
			logger.info("Creating a new cart for project [" + projectId + "]");
			projectMap = new HashMap<>();
			_cart.put(projectId, projectMap);
		}
		Map<Long, Boolean> sampleMap = projectMap.get(sampleId);
		if (sampleMap == null) {
			logger.info("Add sample [" + sampleId + "] to cart for project [" + projectId + "]");
			sampleMap = generateFileMap(sampleId, false);
			projectMap.put(sampleId, sampleMap);
		}
		sampleMap.put(fileId, true);
		return projectMap.size();
	}

	/**
	 * Remove a file from the cart
	 *
	 * @param projectId
	 * 		Id for a {@link Project}
	 * @param sampleId
	 * 		Id for a {@link Sample}
	 * @param fileId
	 * 		Id for a {@link SequenceFile}
	 *
	 * @return The size of the cart for that project.
	 */
	public int removeFileFromCart(Long projectId, Long sampleId, Long fileId) {
		boolean error = false;
		Map<Long, Map<Long, Boolean>> projectMap = _cart.get(projectId);
		if (projectMap == null) {
			error = true;
		} else {
			Map<Long, Boolean> sampleMap = projectMap.get(sampleId);
			if (sampleMap == null) {
				error = true;
			} else {
				sampleMap.put(fileId, false);
				// Check to make sure this sample should still be in the cart
				if (!sampleMap.containsValue(true)) {
					projectMap.remove(sampleId);
				}
			}
		}
		if (error) {
			logger.error("Trying to remove a file from project [" + projectId + "] that is not in the cart");
			return 0;
		} else {
			return projectMap.size();
		}
	}

	private Map<Long, Boolean> generateFileMap(Long sampleId, boolean inCart) {
		Map<Long, Boolean> result = new HashMap<>();
		List<Join<Sample, SequenceFile>> list = sequenceFileService
				.getSequenceFilesForSample(sampleService.read(sampleId));
		for (Join<Sample, SequenceFile> join : list) {
			result.put(join.getObject().getId(), inCart);
		}
		return result;
	}

	private void selectAllFilesInSample(Map<Long, Boolean> map) {
		map.keySet().forEach(id -> map.put(id, true));
	}
}
