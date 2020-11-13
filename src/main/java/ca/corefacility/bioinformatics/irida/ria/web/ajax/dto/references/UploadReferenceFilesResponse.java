package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.references;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

public class UploadReferenceFilesResponse extends AjaxResponse {
	private List<UIReferenceFile> files;

	public UploadReferenceFilesResponse(List<UIReferenceFile> files) {
		this.files = files;
	}

	public List<UIReferenceFile> getFiles() {
		return files;
	}
}
