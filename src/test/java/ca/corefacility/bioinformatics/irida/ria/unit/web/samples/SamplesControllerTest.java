package ca.corefacility.bioinformatics.irida.ria.unit.web.samples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.times;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.files.SequenceFileWebUtilities;
import ca.corefacility.bioinformatics.irida.ria.web.samples.SamplesController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * Created by josh on 14-07-30.
 */
public class SamplesControllerTest {

	// Services
	private SamplesController controller;
	private SampleService sampleService;
	private SequenceFileService sequenceFileService;
	private UserService userService;
	private ProjectService projectService;
	private SequenceFileWebUtilities sequenceFileWebUtilities;

	@Before
	public void setUp() {
		sampleService = mock(SampleService.class);
		sequenceFileService = mock(SequenceFileService.class);
		userService = mock(UserService.class);
		projectService = mock(ProjectService.class);
		sequenceFileWebUtilities = new SequenceFileWebUtilities();
		controller = new SamplesController(sampleService, sequenceFileService, userService, projectService, sequenceFileWebUtilities);
	}

	// ************************************************************************************************
	// PAGE REQUESTS
	// ************************************************************************************************

	@Test
	public void testGetSampleSpecificPage() {
		Model model = new ExtendedModelMap();
		Sample sample = TestDataFactory.constructSample();
		when(sampleService.read(sample.getId())).thenReturn(sample);
		String result = controller.getSampleSpecificPage(model, sample.getId());
		assertEquals("Returns the correct page name", "samples/sample", result);
		assertTrue("Model contains the sample", model.containsAttribute("sample"));
	}

	@Test
	public void testGetEditSampleSpecificPage() {
		Model model = new ExtendedModelMap();
		Sample sample = TestDataFactory.constructSample();
		when(sampleService.read(sample.getId())).thenReturn(sample);
		String result = controller.getEditSampleSpecificPage(model, sample.getId());
		assertEquals("Returns the correct page name", "samples/sample_edit", result);
		assertTrue("Model contains the sample", model.containsAttribute("sample"));
		assertTrue("Model should ALWAYS have an error attribute", model.containsAttribute("errors"));
	}

	@Test
	public void testUpdateSample() {
		Model model = new ExtendedModelMap();
		Sample sample = TestDataFactory.constructSample();
		String organism = "E. coli";
		String geographicLocationName = "The Forks";
		Map<String, Object> updatedValues = ImmutableMap.of(SamplesController.ORGANISM, organism,
				SamplesController.GEOGRAPHIC_LOCATION_NAME, geographicLocationName);
		Map<String, String> update = ImmutableMap.of(SamplesController.ORGANISM, organism,
				SamplesController.GEOGRAPHIC_LOCATION_NAME, geographicLocationName);
		when(sampleService.update(sample.getId(), updatedValues)).thenReturn(sample);
		String result = controller.updateSample(model, sample.getId(), null, update);
		assertEquals("Returns the correct redirect", "redirect:/samples/" + sample.getId(), result);
		assertTrue("Model should be populated with updated attributes",
				model.containsAttribute(SamplesController.ORGANISM));
		assertTrue("Model should be populated with updated attributes",
				model.containsAttribute(SamplesController.GEOGRAPHIC_LOCATION_NAME));
		assertFalse("Model should not be populated with non-updated attributes",
				model.containsAttribute(SamplesController.LATITUDE));
	}

	@Test
	public void testGetSampleFiles() throws IOException {
		ExtendedModelMap model = new ExtendedModelMap();
		String userName = "bob";
		Principal principal = () -> userName;
		Long sampleId = 1l;
		Sample sample = new Sample();
		SequenceFile file = new SequenceFile(Paths.get("/tmp"));
		file.setId(2l);
		User user = new User();
		Project project = new Project();

		@SuppressWarnings("unchecked")
		List<Join<Sample, SequenceFile>> files = Lists.newArrayList(new SampleSequenceFileJoin(sample, file));

		when(sampleService.read(sampleId)).thenReturn(sample);
		when(sequenceFileService.getSequenceFilesForSample(sample)).thenReturn(files);
		when(userService.getUserByUsername(userName)).thenReturn(user);
		when(projectService.getProjectsForSample(sample)).thenReturn(
				Lists.newArrayList(new ProjectSampleJoin(project, sample)));
		when(userService.getUsersForProject(project)).thenReturn(
				(Lists.newArrayList(new ProjectUserJoin(project, user, ProjectRole.PROJECT_OWNER))));

		String sampleFiles = controller.getSampleFiles(model, sampleId, principal);

		assertEquals(SamplesController.SAMPLE_FILES_PAGE, sampleFiles);
		assertTrue((boolean) model.get(SamplesController.MODEL_ATTR_CAN_MANAGE_SAMPLE));

		verify(sampleService, times(2)).read(sampleId);
		verify(sequenceFileService).getSequenceFilesForSample(sample);
	}

	@Test
	public void testGetSampleFilesAsAdmin() throws IOException {
		ExtendedModelMap model = new ExtendedModelMap();
		String userName = "bob";
		Principal principal = () -> userName;
		Long sampleId = 1l;
		Sample sample = new Sample();
		SequenceFile file = new SequenceFile(Paths.get("/tmp"));
		file.setId(2l);
		User user = new User();
		user.setSystemRole(Role.ROLE_ADMIN);

		@SuppressWarnings("unchecked")
		List<Join<Sample, SequenceFile>> files = Lists.newArrayList(new SampleSequenceFileJoin(sample, file));

		when(sampleService.read(sampleId)).thenReturn(sample);
		when(sequenceFileService.getSequenceFilesForSample(sample)).thenReturn(files);
		when(userService.getUserByUsername(userName)).thenReturn(user);

		String sampleFiles = controller.getSampleFiles(model, sampleId, principal);

		assertEquals(SamplesController.SAMPLE_FILES_PAGE, sampleFiles);
		assertTrue((boolean) model.get(SamplesController.MODEL_ATTR_CAN_MANAGE_SAMPLE));

		verify(sampleService, times(2)).read(sampleId);
		verify(sequenceFileService).getSequenceFilesForSample(sample);
		verifyZeroInteractions(projectService);
	}

	@Test
	public void testGetSampleFilesNoAccess() throws IOException {
		ExtendedModelMap model = new ExtendedModelMap();
		String userName = "bob";
		Principal principal = () -> userName;
		Long sampleId = 1l;
		Sample sample = new Sample();
		SequenceFile file = new SequenceFile(Paths.get("/tmp"));
		file.setId(2l);
		User user = new User();
		Project project = new Project();

		@SuppressWarnings("unchecked")
		List<Join<Sample, SequenceFile>> files = Lists.newArrayList(new SampleSequenceFileJoin(sample, file));

		when(sampleService.read(sampleId)).thenReturn(sample);
		when(sequenceFileService.getSequenceFilesForSample(sample)).thenReturn(files);
		when(userService.getUserByUsername(userName)).thenReturn(user);
		when(projectService.getProjectsForSample(sample)).thenReturn(
				Lists.newArrayList(new ProjectSampleJoin(project, sample)));
		when(userService.getUsersForProject(project)).thenReturn(
				(Lists.newArrayList(new ProjectUserJoin(project, user, ProjectRole.PROJECT_USER))));

		String sampleFiles = controller.getSampleFiles(model, sampleId, principal);

		assertEquals(SamplesController.SAMPLE_FILES_PAGE, sampleFiles);
		assertFalse((boolean) model.get(SamplesController.MODEL_ATTR_CAN_MANAGE_SAMPLE));

		verify(sampleService, times(2)).read(sampleId);
		verify(sequenceFileService).getSequenceFilesForSample(sample);
	}

	@Test
	public void testRemoveFileFromSample() {
		Long sampleId = 1l;
		Long fileId = 2l;
		Sample sample = new Sample();
		SequenceFile file = new SequenceFile(Paths.get("/tmp"));

		when(sampleService.read(sampleId)).thenReturn(sample);
		when(sequenceFileService.read(fileId)).thenReturn(file);

		controller.removeFileFromSample(sampleId, fileId);

		verify(sampleService).removeSequenceFileFromSample(sample, file);
	}

	// ************************************************************************************************
	// AJAX REQUESTS
	// ************************************************************************************************

	@Test
	public void testGetFilesForSample() throws IOException {
		Sample sample = TestDataFactory.constructSample();
		List<Join<Sample, SequenceFile>> joinList = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Path path = Paths.get("/tmp/sequence-files/fake-file" + i + ".fast");
			SequenceFile file = new SequenceFile(path);
			file.setId(1L + i);
			joinList.add(new SampleSequenceFileJoin(sample, file));
		}
		when(sampleService.read(1L)).thenReturn(sample);
		when(sequenceFileService.getSequenceFilesForSample(sample)).thenReturn(joinList);
		List<Map<String, Object>> result = controller.getFilesForSample(1L);
		assertEquals("Should have the correct number of sequence file records.", joinList.size(), result.size());

		Map<String, Object> file1 = result.get(0);
		assertTrue("File has an id", file1.containsKey("id"));
		assertTrue("File has an name", file1.containsKey("label"));
		assertTrue("File has an created", file1.containsKey("createdDate"));
	}
}
