package ca.corefacility.bioinformatics.irida.web.controller.api.samples;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.Optional;

@Controller
public class RESTSampleAssemblyController {

	private SampleService sampleService;

	@Autowired
	public RESTSampleAssemblyController(SampleService sampleService) {
		this.sampleService = sampleService;
	}

	@RequestMapping("/api/samples/{sampleId}/assemblies")
	public ModelMap listAssembliesForSample(@PathVariable Long sampleId){
		ModelMap modelMap = new ModelMap();

		Sample sample = sampleService.read(sampleId);
		Collection<SampleGenomeAssemblyJoin> assembliesForSample = sampleService.getAssembliesForSample(sample);

		ResourceCollection<GenomeAssembly> assemblyResources = new ResourceCollection<>(assembliesForSample.size());

		for(SampleGenomeAssemblyJoin join : assembliesForSample){
			assemblyResources.add(join.getObject());
		}

		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, assemblyResources);

		return modelMap;
	}

	@RequestMapping("/api/samples/{sampleId}/assemblies/{assemblyId}")
	public ModelMap readAssemblyForSample(@PathVariable Long sampleId, @PathVariable Long assemblyId) {
		ModelMap modelMap = new ModelMap();

		Sample sample = sampleService.read(sampleId);
		Collection<SampleGenomeAssemblyJoin> assembliesForSample = sampleService.getAssembliesForSample(sample);

		Optional<GenomeAssembly> genomeAssemblyOpt = assembliesForSample.stream()
				.filter(a -> a.getObject()
						.getId()
						.equals(assemblyId))
				.findFirst()
				.map(j -> j.getObject());

		if (!genomeAssemblyOpt.isPresent()) {
			throw new EntityNotFoundException(
					"Could not get an assembly with id " + assemblyId + " for sample " + sampleId);
		}

		GenomeAssembly genomeAssembly = genomeAssemblyOpt.get();

		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, genomeAssembly);

		return modelMap;
	}
}
