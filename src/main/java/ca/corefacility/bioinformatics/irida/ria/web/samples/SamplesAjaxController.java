package ca.corefacility.bioinformatics.irida.ria.web.samples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import ca.corefacility.bioinformatics.irida.model.assembly.UploadedAssembly;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.service.GenomeAssemblyService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

@RestController
@RequestMapping("/ajax/samples")
public class SamplesAjaxController {
	private final SampleService sampleService;
	private final SequencingObjectService sequencingObjectService;
	private final GenomeAssemblyService genomeAssemblyService;
	private final MessageSource messageSource;

	@Autowired
	public SamplesAjaxController(SampleService sampleService, SequencingObjectService sequencingObjectService,
			GenomeAssemblyService genomeAssemblyService, MessageSource messageSource) {
		this.sampleService = sampleService;
		this.sequencingObjectService = sequencingObjectService;
		this.genomeAssemblyService = genomeAssemblyService;
		this.messageSource = messageSource;
	}

	/**
	 * Upload {@link SequenceFile}'s to a sample
	 *
	 * @param sampleId The {@link Sample} id to upload to
	 * @param request  The current request which contains {@link MultipartFile}
	 * @param locale   The locale for the currently logged in user
	 */
	@RequestMapping(value = { "/{sampleId}/sequenceFiles/upload" }, method = RequestMethod.POST)
	public ResponseEntity<String> uploadSequenceFiles(@PathVariable Long sampleId, MultipartHttpServletRequest request,
			Locale locale) {
		Sample sample = sampleService.read(sampleId);

		Iterator<String> fileNames = request.getFileNames();
		List<MultipartFile> files = new ArrayList<>();
		while (fileNames.hasNext()) {
			files.add(request.getFile(fileNames.next()));
		}

		final Map<String, List<MultipartFile>> pairedFiles = SamplePairer.getPairedFiles(files);
		final List<MultipartFile> singleFiles = SamplePairer.getSingleFiles(files);

		try {
			for (String key : pairedFiles.keySet()) {
				List<MultipartFile> list = pairedFiles.get(key);
				createSequenceFilePairsInSample(list, sample);
			}

			for (MultipartFile file : singleFiles) {
				createSequenceFileInSample(file, sample);
			}
			return ResponseEntity.ok()
					.body(messageSource.getMessage("server.SampleFileUploader.success",
							new Object[] { sample.getSampleName() }, locale));
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(messageSource.getMessage("server.SampleFileUploader.error",
							new Object[] { sample.getSampleName() }, locale));
		}
	}

	/**
	 * Upload assemblies to the given sample
	 *
	 * @param sampleId the ID of the sample to upload to
	 * @param request  The current request which contains {@link MultipartFile}
	 * @param locale   The locale for the currently logged in user
	 */
	@RequestMapping(value = { "/{sampleId}/assemblies/upload" }, method = RequestMethod.POST)
	public ResponseEntity<String> uploadAssemblies(@PathVariable Long sampleId, MultipartHttpServletRequest request,
			Locale locale) {
		Sample sample = sampleService.read(sampleId);
		Iterator<String> fileNames = request.getFileNames();
		List<MultipartFile> files = new ArrayList<>();
		while (fileNames.hasNext()) {
			files.add(request.getFile(fileNames.next()));
		}

		try {
			for (MultipartFile file : files) {
				Path temp = Files.createTempDirectory(null);
				Path target = temp.resolve(file.getOriginalFilename());
				file.transferTo(target.toFile());
				UploadedAssembly uploadedAssembly = new UploadedAssembly(target);

				genomeAssemblyService.createAssemblyInSample(sample, uploadedAssembly);
			}
			return ResponseEntity.ok()
					.body(messageSource.getMessage("server.SampleFileUploader.success",
							new Object[] { sample.getSampleName() }, locale));
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(messageSource.getMessage("server.SampleFileUploader.error",
							new Object[] { sample.getSampleName() }, locale));
		}
	}

	/**
	 * Create {@link SequenceFile}'s then add them as {@link SequenceFilePair}
	 * to a {@link Sample}
	 *
	 * @param pair   {@link List} of {@link MultipartFile}
	 * @param sample {@link Sample} to add the pair to.
	 * @throws IOException Exception thrown if there is an error handling the file.
	 */
	private void createSequenceFilePairsInSample(List<MultipartFile> pair, Sample sample) throws IOException {
		SequenceFile firstFile = createSequenceFile(pair.get(0));
		SequenceFile secondFile = createSequenceFile(pair.get(1));
		sequencingObjectService.createSequencingObjectInSample(new SequenceFilePair(firstFile, secondFile), sample);
	}

	/**
	 * Create a {@link SequenceFile} and add it to a {@link Sample}
	 *
	 * @param file   {@link MultipartFile}
	 * @param sample {@link Sample} to add the file to.
	 * @throws IOException Exception thrown if there is an error handling the file.
	 */
	private void createSequenceFileInSample(MultipartFile file, Sample sample) throws IOException {
		SequenceFile sequenceFile = createSequenceFile(file);
		sequencingObjectService.createSequencingObjectInSample(new SingleEndSequenceFile(sequenceFile), sample);
	}

	/**
	 * Private method to move the sequence file into the correct directory and
	 * create the {@link SequenceFile} object.
	 *
	 * @param file {@link MultipartFile} sequence file uploaded.
	 * @return {@link SequenceFile}
	 * @throws IOException Exception thrown if there is an error handling the file.
	 */
	private SequenceFile createSequenceFile(MultipartFile file) throws IOException {
		Path temp = Files.createTempDirectory(null);
		Path target = temp.resolve(file.getOriginalFilename());
		file.transferTo(target.toFile());
		return new SequenceFile(target);
	}
}
