package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.ria.web.launchPipeline.dtos.LaunchSample;

@Component
public class UIPipelineSampleService {
	private final UISampleService sampleService;
	private final UICartService cartService;

	@Autowired
	public UIPipelineSampleService(UISampleService sampleService, UICartService cartService) {
		this.sampleService = sampleService;
		this.cartService = cartService;
	}

	public List<LaunchSample> getPipelineSamples(boolean paired, boolean singles) {
		Map<Project, List<Sample>> cart = cartService.getFullCart();
		List<LaunchSample> samples = new ArrayList<>();
		cart.forEach((project, projectSamples) -> {
			for (Sample sample : projectSamples) {
				LaunchSample ls = new LaunchSample(sample, project);
				List<SequencingObject> files = new ArrayList<>();
				if (paired) {
					files.addAll(sampleService.getPairedSequenceFilesForSample(sample, project));
				}
				if (singles) {
					files.addAll(sampleService.getSingleEndSequenceFilesForSample(sample, project));
				}
				ls.setFiles(files);
				samples.add(ls);
			}
		});
		return samples;
	}
}
