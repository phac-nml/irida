package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import java.util.List;

/**
 * Handles data transfer for sharing samples between projects
 */
public class ShareSamplesRequest {
	private Long currentId;
	private Long targetId;
	private List<Long> sampleIds;
	private Boolean locked;
	private Boolean remove;

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
}
