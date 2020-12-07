package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.launchPipeline.dtos.LaunchSamples;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.SampleFiles;

@Component
public class UIPipelineSampleService {
	private final UISampleService sampleService;
	private final UICartService cartService;

	@Autowired
	public UIPipelineSampleService(UISampleService sampleService, UICartService cartService) {
		this.sampleService = sampleService;
		this.cartService = cartService;
	}

	public List<LaunchSamples> getPipelineSamples() {
		Map<Project, List<Sample>> cart = cartService.getFullCart();
		List<LaunchSamples> samples = new ArrayList<>();
		cart.forEach((key, value) -> {
			for (Sample sample : value) {
				SampleFiles files = sampleService.getSampleFiles(sample.getId(), key.getId());
				samples.add(new LaunchSamples((sample), key, files));
			}
		});
		return samples;
	}
}
