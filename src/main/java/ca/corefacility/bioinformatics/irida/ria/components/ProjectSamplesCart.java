package ca.corefacility.bioinformatics.irida.ria.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Component
public class ProjectSamplesCart {
	private Map<Long, Map<Long, Boolean>> _cart;

	/*
	 * SERVICES
	 */
	private SampleService sampleService;
	private SequenceFileService sequenceFileService;

	@Autowired
	public ProjectSamplesCart(SampleService sampleService, SequenceFileService sequenceFileService) {
		this.sampleService = sampleService;
		this.sequenceFileService = sequenceFileService;
		_cart = new HashMap<>();
	}

	/**
	 * Clear the cart.
	 */
	public void empty() {
		_cart.clear();
	}

	/**
	 * Add a sample id to the cart. Should not be able to add the sample id twice.  If the same one is added, all the
	 * files are checked to make sure they are selected as well.
	 *
	 * @param sampleId
	 * 		The id for the sample to add.
	 * @return The number of samples in the cart.
	 */
	public int addSampleToCart(Long sampleId) {
		if (_cart.containsKey(sampleId)) {
			Map<Long, Boolean> files = _cart.get(sampleId);
			for (Long key : files.keySet()) {
				files.put(key, true);
			}
		} else {
			_cart.put(sampleId, _generateFileMap(sampleId, true));
		}
		return _cart.size();
	}

	/**
	 * Remove a sample id from the cart.
	 *
	 * @param sampleId
	 * 		Id to remove
	 * @return The number of samples in the cart.
	 */
	public int removeSampleFromCart(Long sampleId) {
		_cart.remove(sampleId);
		return _cart.size();
	}

	/**
	 * Mark a sequence file as inactive.
	 *
	 * @param sampleId
	 * 		Id for the sample that contains the sequence file.
	 * @param fileId
	 * 		Id for the file.
	 * @return The number of files that are active.
	 */
	public int markSequenceFileAsInactive(Long sampleId, Long fileId) {
		// To omit a file, the sample should already be in the cart.
		// Or it really doesn't matter.
		if (!_cart.containsKey(sampleId)) {
			return 0;
		}
		Map<Long, Boolean> files = _cart.get(sampleId);
		files.put(fileId, false);
		int count = files.size();

		// If count is 0 than the sample should not be selected
		if (count == 0) {
			_cart.remove(sampleId);
		}
		return count;
	}

	/**
	 * Mark a file as active. File must have been previously inactivated or sample was not selected.
	 *
	 * @param sampleId
	 * 		Id for the sample that contains the file.
	 * @param fileId
	 * 		Id for the file.
	 * @return The number of files that are active.x
	 */
	public int markSequenceFileAsActive(Long sampleId, Long fileId) {
		// Check to see if the sample is in the cart yet.
		if (!_cart.containsKey(sampleId)) {
			_cart.put(sampleId, _generateFileMap(sampleId, false));
		}
		int count = 0;
		Map<Long, Boolean> files = _cart.get(sampleId);
		for (Long id : files.keySet()) {
			if (id.equals(fileId)) {
				count++;
				files.put(id, true);
			} else if (files.get(id).equals(true)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Get a list of the files selected for a given file.
	 *
	 * @param sampleId
	 * 		Id for the sample.
	 * @return List of file ids
	 */
	public List<Long> getSelectedFilesForSample(Long sampleId) {
		if (!_cart.containsKey(sampleId)) {
			return new ArrayList<>(0);
		} else {
			List<Long> selected = new ArrayList<>();
			Map<Long, Boolean> files = _cart.get(sampleId);
			selected.addAll(files.keySet().stream().filter(id -> files.get(id)).collect(Collectors.toList()));
			return selected;
		}
	}

	/**
	 * Determine if a sample is in the cart.
	 *
	 * @param sampleId
	 * 		Id for the sample to check.
	 * @return True if sample in the cart.
	 */
	public boolean isSampleSelected(long sampleId) {
		return _cart.containsKey(sampleId);
	}

	/**
	 * Determine if a file is in the cart.
	 *
	 * @param sampleId
	 * 		Id for the sample that the file should be in.
	 * @param fileId
	 * 		Id for the file.
	 * @return True if the file is in the cart.
	 */
	public boolean isFileSelected(long sampleId, long fileId) {
		if (!_cart.containsKey(sampleId)) {
			return false;
		} else {
			return _cart.get(sampleId).get(fileId);
		}
	}

	/**
	 * Get a list of all sample ids in the cart.
	 *
	 * @return List of sample ids.
	 */
	public Set<Long> getSelectedSampleIds() {
		return _cart.keySet();
	}

	/**
	 * Get the number of samples in the cart.
	 *
	 * @return The number of samples in the cart.
	 */
	public int getSelectedCount() {
		return _cart.size();
	}

	private Map<Long, Boolean> _generateFileMap(Long sampleId, boolean selected) {
		Sample sample = sampleService.read(sampleId);
		Map<Long, Boolean> fileMap = new HashMap<>();
		for (Join<Sample, SequenceFile> join : sequenceFileService.getSequenceFilesForSample(sample)) {
			fileMap.put(join.getObject().getId(), selected);
		}
		return fileMap;
	}
}
