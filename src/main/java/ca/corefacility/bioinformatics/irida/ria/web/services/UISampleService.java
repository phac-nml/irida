package ca.corefacility.bioinformatics.irida.ria.web.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.SampleDetails;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

@Component
public class UISampleService {
	private final SampleService sampleService;
	private final UpdateSamplePermission updateSamplePermission;
	private final UICartService cartService;

	@Autowired
	public UISampleService(SampleService sampleService, UpdateSamplePermission updateSamplePermission,
			UICartService cartService) {
		this.sampleService = sampleService;
		this.updateSamplePermission = updateSamplePermission;
		this.cartService = cartService;
	}

	/**
	 * Get full details, includig metadata for a {@link Sample}
	 *
	 * @param id Identifier for a {@link Sample}
	 * @return {@link SampleDetails}
	 */
	public SampleDetails getSampleDetails(Long id) {
		Sample sample = sampleService.read(id);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isModifiable = updateSamplePermission.isAllowed(authentication, sample);
		return new SampleDetails(sample, isModifiable, cartService.isSampleInCart(id));
	}
}
