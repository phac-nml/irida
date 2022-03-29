package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.SampleAnalyses;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

/**
 * UI Service for analyses
 */

@Component
public class UIAnalysesService {
	private final SampleService sampleService;
	private final SequencingObjectService sequencingObjectService;
	private final UserService userService;
	private final AnalysisSubmissionRepository analysisSubmissionRepository;
	private final IridaWorkflowsService iridaWorkflowsService;
	private final MessageSource messageSource;

	@Autowired
	public UIAnalysesService(SampleService sampleService, SequencingObjectService sequencingObjectService,
			UserService userService, AnalysisSubmissionRepository analysisSubmissionRepository,
			IridaWorkflowsService iridaWorkflowsService, MessageSource messageSource) {
		this.sampleService = sampleService;
		this.sequencingObjectService = sequencingObjectService;
		this.userService = userService;
		this.analysisSubmissionRepository = analysisSubmissionRepository;
		this.iridaWorkflowsService = iridaWorkflowsService;
		this.messageSource = messageSource;
	}

	/**
	 * Get analyses for sample
	 *
	 * @param sampleId  Identifier for a sample
	 * @param principal The currently logged on user
	 * @param locale    User's locale
	 * @return {@link SampleAnalyses} containing a list of analyses for the sample
	 */
	public List<SampleAnalyses> getSampleAnalyses(Long sampleId, Principal principal, Locale locale) {
		Sample sample = sampleService.read(sampleId);
		List<SampleAnalyses> sampleAnalysesList = new ArrayList<>();

		Collection<SampleSequencingObjectJoin> sampleSequencingObjectJoins = sequencingObjectService.getSequencingObjectsForSample(
				sample);
		List<SequencingObject> sequencingObjectList = sampleSequencingObjectJoins.stream()
				.map(s -> s.getObject())
				.collect(Collectors.toList());

		User user = userService.getUserByUsername(principal.getName());

		for (SequencingObject sequencingObject : sequencingObjectList) {

			Set<AnalysisSubmission> analysisSubmissionSet;

			if (!user.getSystemRole()
					.equals(Role.ROLE_ADMIN)) {
				analysisSubmissionSet = analysisSubmissionRepository.findAnalysisSubmissionsForSequencingObjectBySubmitter(
						sequencingObject, user);
			} else {
				analysisSubmissionSet = analysisSubmissionRepository.findAnalysisSubmissionsForSequencingObject(
						sequencingObject);
			}

			for (AnalysisSubmission analysisSubmission : analysisSubmissionSet) {
				IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflowOrUnknown(analysisSubmission);
				String analysisType = iridaWorkflow.getWorkflowDescription()
						.getAnalysisType()
						.getType();
				analysisType = messageSource.getMessage("workflow." + analysisType + ".title", null, analysisType,
						locale);

				sampleAnalysesList.add(new SampleAnalyses(analysisSubmission, analysisType));
			}

		}
		return sampleAnalysesList;
	}
}
