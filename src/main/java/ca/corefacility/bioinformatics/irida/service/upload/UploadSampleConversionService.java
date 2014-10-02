package ca.corefacility.bioinformatics.irida.service.upload;

import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;

/**
 * Used to convert IRIDA Samples to those that can be uploaded into Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface UploadSampleConversionService {
	
	/**
	 * Converts the passed {@link Sample} object to an {@link UploadSample}.
	 *
	 * @param sample
	 *            The sample to convert
	 * @return The corresponding UploadSample.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#sample, 'canReadSample')")
	public UploadSample convertToUploadSample(Sample sample);
}
