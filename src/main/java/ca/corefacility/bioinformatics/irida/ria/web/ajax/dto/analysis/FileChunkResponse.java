package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.analysis;

/**
 * Used to send the text from the chunk requested to the UI
 */

public class FileChunkResponse {
	private String text;
	private Long filePointer;

	public FileChunkResponse(String text, Long filePointer) {
		this.text = text;
		this.filePointer = filePointer;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getFilePointer() {
		return filePointer;
	}

	public void setFilePointer(Long filePointer) {
		this.filePointer = filePointer;
	}
}
