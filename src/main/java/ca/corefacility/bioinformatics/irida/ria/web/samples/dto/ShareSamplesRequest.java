package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import java.util.List;

/**
 * Handles data transfer for sharing samples between projects
 */
public class ShareSamplesRequest {
	/**
	 * Project identifier to share / move samples from
	 */
	private Long currentId;

	/**
	 * Project identifier to share / move samples to
	 */
	private Long targetId;

	/**
	 * Sample identifiers for all samples to share / move
	 */
	private List<Long> sampleIds;

	/**
	 * Flag if the samples are to be locked (unmodifiable) in the destination project.
	 */
	private Boolean locked;

	/**
	 * Flag, if true samples will be removed from the current project
	 * This is the "move" function
	 */
	private Boolean remove;

	private List<ShareMetadataRestriction> restrictions;

	public Long getCurrentId() {
		return currentId;
	}

	public void setCurrentId(Long currentId) {
		this.currentId = currentId;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	public List<Long> getSampleIds() {
		return sampleIds;
	}

	public void setSampleIds(List<Long> sampleIds) {
		this.sampleIds = sampleIds;
	}

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public Boolean getRemove() {
		return remove;
	}

	public void setRemove(Boolean remove) {
		this.remove = remove;
	}

	public List<ShareMetadataRestriction> getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(List<ShareMetadataRestriction> restrictions) {
		this.restrictions = restrictions;
	}
}
