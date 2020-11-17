package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.references;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

/**
 * Used by UI to handle the return of a successful upload on one or more reference files.
 */
public class UploadReferenceFilesResponse extends AjaxResponse {
	private List<UIReferenceFile> files;

	public UploadReferenceFilesResponse(List<UIReferenceFile> files) {
		this.files = files;
	}

	public List<UIReferenceFile> getFiles() {
		return files;
	}
}
