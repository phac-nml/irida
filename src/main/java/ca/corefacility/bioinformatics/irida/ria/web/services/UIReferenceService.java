package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import ca.corefacility.bioinformatics.irida.exceptions.UnsupportedReferenceFileContentError;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.UIReferenceFile;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;

/**
 * Service to handle Reference Files through the UI.
 */
@Component
public class UIReferenceService {
	private final ReferenceFileService referenceFileService;

	@Autowired
	public UIReferenceService(ReferenceFileService referenceFileService) {
		this.referenceFileService = referenceFileService;
	}

	public UIReferenceFile addReferenceFile(MultipartFile file) throws IOException, NullPointerException,
			UnsupportedReferenceFileContentError {
		Path path = Files.createTempDirectory(null);
		Path targetPath = path.resolve(Objects.requireNonNull(file.getOriginalFilename()));
		file.transferTo(targetPath.toFile());
		ReferenceFile referenceFile = new ReferenceFile(targetPath);
		referenceFile = referenceFileService.create(referenceFile);

		// Clean up temporary objects
		try {
			Files.delete(path);
			Files.delete(targetPath);
		} catch (IOException e) {
			// Just an error cleaning up the files.
		}

		return new UIReferenceFile(referenceFile);
	}
}
