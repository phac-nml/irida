package ca.corefacility.bioinformatics.irida.web.controller.api.samples;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssemblyFromAnalysis;
import ca.corefacility.bioinformatics.irida.model.assembly.UploadedAssembly;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.repositories.assembly.GenomeAssemblyRepository;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sequencefile.SequenceFileResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTAnalysisSubmissionController;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectSamplesController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.common.net.HttpHeaders;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Controller for viewing and downloading assemblies for samples
 */
@Controller
public class RESTSampleAssemblyController {

	private static final Logger logger = LoggerFactory.getLogger(RESTSampleAssemblyController.class);

	public static final String REL_SAMPLE = "sample";
	public static final String REL_SAMPLE_ASSEMBLIES = "sample/assemblies";
	public static final String REL_ASSEMBLY_ANALYSIS = "assembly/analysis";

	private SampleService sampleService;

	private GenomeAssemblyRepository assemblyRepository;

	@Autowired
	public RESTSampleAssemblyController(SampleService sampleService, GenomeAssemblyRepository assemblyRepository) {
		this.sampleService = sampleService;
		this.assemblyRepository = assemblyRepository;
	}

	/**
	 * List all the {@link GenomeAssembly}s for a given {@link Sample}
	 *
	 * @param sampleId the id of the sample
	 * @return a list of details about the assemblies
	 */
	@RequestMapping("/api/samples/{sampleId}/assemblies")
	public ModelMap listAssembliesForSample(@PathVariable Long sampleId) {
		ModelMap modelMap = new ModelMap();

		Sample sample = sampleService.read(sampleId);
		Collection<SampleGenomeAssemblyJoin> assembliesForSample = sampleService.getAssembliesForSample(sample);

		ResourceCollection<GenomeAssembly> assemblyResources = new ResourceCollection<>(assembliesForSample.size());

		for (SampleGenomeAssemblyJoin join : assembliesForSample) {
			GenomeAssembly genomeAssembly = join.getObject();

			genomeAssembly.add(getLinksForAssembly(genomeAssembly, sampleId));

			assemblyResources.add(genomeAssembly);
		}

		assemblyResources.add(
				linkTo(methodOn(RESTSampleAssemblyController.class).listAssembliesForSample(sampleId)).withSelfRel());
		assemblyResources.add(
				linkTo(methodOn(RESTProjectSamplesController.class).getSample(sampleId)).withRel(REL_SAMPLE));

		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, assemblyResources);

		return modelMap;
	}

	/**
	 * Read an individual {@link GenomeAssembly} for a given {@link Sample}
	 *
	 * @param sampleId   the id of the sample
	 * @param assemblyId the id of the assembly
	 * @return details about the requested assembly
	 */
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

		genomeAssembly.add(getLinksForAssembly(genomeAssembly, sampleId));

		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, genomeAssembly);

		return modelMap;
	}

	/**
	 * Upload a new {@link GenomeAssembly} and add it to a {@link Sample}
	 *
	 * @param sampleId The ID of the sampleto add the assembly to
	 * @param file     the file content
	 * @param response A response to send to the user
	 * @return a model with links to the created assembly
	 * @throws IOException if the upload fails
	 */
	@RequestMapping(value = "/api/samples/{sampleId}/assemblies", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ModelMap addNewAssemblyToSample(@PathVariable Long sampleId, @RequestPart("file") MultipartFile file,
			HttpServletResponse response) throws IOException {
		ModelMap modelMap = new ModelMap();
		logger.debug("Adding assembly file to sample " + sampleId);
		logger.trace("Uploaded file size: " + file.getSize() + " bytes");

		Sample sample = sampleService.read(sampleId);
		logger.trace("Read sample " + sampleId);

		Path temp = Files.createTempDirectory(null);
		Path target = temp.resolve(file.getOriginalFilename());

		try {
			file.transferTo(target.toFile());
			logger.trace("Wrote temp file to " + target);

			//create the assembly with the temp file
			UploadedAssembly uploadedAssembly = new UploadedAssembly(target);

			//save the new assembly
			SampleGenomeAssemblyJoin assemblyInSample = sampleService.createAssemblyInSample(sample, uploadedAssembly);

			GenomeAssembly savedAssembly = assemblyInSample.getObject();

			//get links for the assembly
			Collection<Link> linksForAssembly = getLinksForAssembly(savedAssembly, sampleId);
			savedAssembly.add(linksForAssembly);
			modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, savedAssembly);
			String selfHref = savedAssembly.getSelfHref();

			//set the response headers and status
			response.addHeader(HttpHeaders.LOCATION, selfHref);
			response.setStatus(HttpStatus.CREATED.value());

		} catch (IllegalArgumentException e) {
			logger.debug("Error 400 - Bad Request: " + e.getMessage());
			throw e;
		} finally {
			// clean up the temporary files.
			logger.trace("Deleted temp files");
			Files.deleteIfExists(target);
			Files.deleteIfExists(temp);
		}

		return modelMap;
	}

	/**
	 * Generate the links for a usual {@link GenomeAssembly}
	 *
	 * @param assembly the assembly to get links for
	 * @param sampleId the sampleid associaed with the assembly
	 * @return a collection of links
	 */
	private Collection<Link> getLinksForAssembly(GenomeAssembly assembly, Long sampleId) {
		List<Link> links = new ArrayList<>();

		//link to the resource itself
		links.add(linkTo(methodOn(RESTSampleAssemblyController.class).readAssemblyForSample(sampleId,
				assembly.getId())).withSelfRel());

		// if this assembly came from an analysis, link to it
		if (assembly instanceof GenomeAssemblyFromAnalysis) {
			GenomeAssemblyFromAnalysis analysisAssembly = (GenomeAssemblyFromAnalysis) assembly;

			links.add(linkTo(methodOn(RESTAnalysisSubmissionController.class).getResource(
					analysisAssembly.getAnalysisSubmission()
							.getId())).withRel(REL_ASSEMBLY_ANALYSIS));
		}

		return links;
	}
}
