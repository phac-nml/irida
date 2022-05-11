package ca.corefacility.bioinformatics.irida.ria.unit.web.projects;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.mock.web.MockHttpServletResponse;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesParams;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.UISampleFilter;
import ca.corefacility.bioinformatics.irida.ria.web.models.datatables.DTProjectSamples;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectSamplesController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ProjectSamplesControllerTest {
	public static final String PROJECT_ORGANISM = "E. coli";
	public static final String FILE_PATH = "src/test/resources/files/test_file.fastq";
	private static final String USER_NAME = "testme";
	private static final User user = new User(USER_NAME, null, null, null, null, null);
	private static final String PROJECT_NAME = "test_project";
	private static final Long PROJECT_ID = 1L;
	private static final Long PROJECT_MODIFIED_DATE = 1403723706L;
	private static Project project = null;
	// Services
	private ProjectService projectService;
	private ProjectSamplesController controller;
	private SampleService sampleService;
	private SequencingObjectService sequencingObjectService;
	private MessageSource messageSource;
	private ProjectControllerUtils projectUtils;

	@BeforeEach
	public void setUp() {
		projectService = mock(ProjectService.class);
		sampleService = mock(SampleService.class);
		sequencingObjectService = mock(SequencingObjectService.class);
		projectUtils = mock(ProjectControllerUtils.class);
		messageSource = mock(MessageSource.class);

		controller = new ProjectSamplesController(projectService, sampleService, sequencingObjectService,
				projectUtils, messageSource);
		user.setId(1L);

		mockSidebarInfo();
	}

	/**
	 * Mocks the information found within the project sidebar.
	 */
	private void mockSidebarInfo() {
		Project project = getProject();
		Collection<Join<Project, User>> ownerList = new ArrayList<>();
		ownerList.add(new ProjectUserJoin(project, user, ProjectRole.PROJECT_OWNER));
		when(projectService.read(PROJECT_ID)).thenReturn(project);
	}

	private Project getProject() {
		if (project == null) {
			project = new Project(PROJECT_NAME);
			project.setId(PROJECT_ID);
			project.setOrganism(PROJECT_ORGANISM);
			project.setModifiedDate(new Date(PROJECT_MODIFIED_DATE));
		}
		return project;
	}


	@SuppressWarnings("unchecked")
	@Test
	public void testMoveSampleToProject() {
		Long projectId = 1L;
		List<Long> sampleIds = ImmutableList.of(2L, 3L);
		Long newProjectId = 4L;
		boolean removeFromOriginal = true;
		Project oldProject = new Project("oldProject");
		Project newProject = new Project("newProject");
		Sample s2 = new Sample("s2");
		Sample s3 = new Sample("s3");
		ArrayList<Sample> sampleList = Lists.newArrayList(s2, s3);
		boolean owner = true;
		ArrayList<ProjectSampleJoin> joins = Lists.newArrayList(new ProjectSampleJoin(newProject, s2, owner),
				new ProjectSampleJoin(newProject, s3, owner));

		when(projectService.read(projectId)).thenReturn(oldProject);
		when(projectService.read(newProjectId)).thenReturn(newProject);
		when(sampleService.readMultiple(any(Iterable.class))).thenReturn(sampleList);
		when(projectService.moveSamples(oldProject, newProject, sampleList)).thenReturn(joins);

		Map<String, Object> result = controller.shareSampleToProject(projectId, sampleIds, newProjectId,
				removeFromOriginal, true, Locale.US);

		assertTrue(result.containsKey("result"));
		assertTrue(result.containsKey("message"));

		verify(projectService).read(projectId);
		verify(projectService).read(newProjectId);

		verify(projectService).moveSamples(oldProject, newProject, sampleList);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testShareSampleToProject() {
		Long projectId = 1L;
		List<Long> sampleIds = ImmutableList.of(2L, 3L);
		Long newProjectId = 4L;
		boolean removeFromOriginal = false;
		Project oldProject = new Project("oldProject");
		Project newProject = new Project("newProject");
		Sample s2 = new Sample("s2");
		Sample s3 = new Sample("s3");
		ArrayList<Sample> sampleList = Lists.newArrayList(s2, s3);
		boolean owner = true;
		ArrayList<ProjectSampleJoin> joins = Lists.newArrayList(new ProjectSampleJoin(newProject, s2, owner),
				new ProjectSampleJoin(newProject, s3, owner));

		when(projectService.read(projectId)).thenReturn(oldProject);
		when(projectService.read(newProjectId)).thenReturn(newProject);
		when(sampleService.readMultiple(any(Iterable.class))).thenReturn(sampleList);
		when(projectService.shareSamples(oldProject, newProject, sampleList, owner)).thenReturn(joins);

		controller.shareSampleToProject(projectId, sampleIds, newProjectId, removeFromOriginal, true, Locale.US);

		verify(projectService).read(projectId);
		verify(projectService).read(newProjectId);

		verify(projectService).shareSamples(oldProject, newProject, sampleList, owner);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testShareSampleToProjectSampleExists() {
		Long projectId = 1L;
		List<Long> sampleIds = ImmutableList.of(2L, 3L);
		Long newProjectId = 4L;
		boolean removeFromOriginal = false;
		Project oldProject = new Project("oldProject");
		Project newProject = new Project("newProject");
		Sample s2 = new Sample("s2");
		Sample s3 = new Sample("s3");
		ArrayList<Sample> sampleList = Lists.newArrayList(s2, s3);
		boolean owner = true;

		when(projectService.read(projectId)).thenReturn(oldProject);
		when(projectService.read(newProjectId)).thenReturn(newProject);
		when(sampleService.readMultiple(any(Iterable.class))).thenReturn(sampleList);

		when(projectService.shareSamples(oldProject, newProject, sampleList, owner)).thenThrow(
				new EntityExistsException("that sample exists in the project"));


		Map<String, Object> copySampleToProject = controller.shareSampleToProject(projectId, sampleIds, newProjectId,
				removeFromOriginal, true, Locale.US);

		assertTrue(copySampleToProject.containsKey("warnings"));

		verify(projectService).read(projectId);
		verify(projectService).read(newProjectId);
	}

	@Test
	public void testDeleteProjectSamples() {
		Project project1 = getProject();
		Sample sample = new Sample("test");
		sample.setId(1L);
		projectService.addSampleToProject(project1, sample, true);
		List<Long> idList = new ArrayList<>();
		idList.add(1L);
		when(projectService.read(PROJECT_ID)).thenReturn(project1);
		when(sampleService.read(anyLong())).thenReturn(sample);
		Map<String, Object> result = controller.deleteProjectSamples(PROJECT_ID, idList, Locale.US);
		assertTrue(result.containsKey("result"), "Result contains the word success");
		verify(projectService).removeSampleFromProject(project1, sample);
	}

	@Test
	public void testAjaxSamplesMerge() {
		String newName = "FRED";
		Project project = TestDataFactory.constructProject();
		Sample sample1 = new Sample("Wilma");
		sample1.setId(1L);
		sample1.setSampleName(newName);
		Sample sample2 = new Sample("Betty");
		sample2.setId(11L);
		List<Long> sampleIds = new ArrayList<>();
		sampleIds.add(1L);
		sampleIds.add(11L);

		when(sampleService.read(1L)).thenReturn(sample1);
		when(sampleService.read(11L)).thenReturn(sample2);
		when(projectService.read(TestDataFactory.PROJECT_ID)).thenReturn(project);
		when(sampleService.update(any(Sample.class))).thenReturn(sample1);

		// Call the controller with a new name
		Map<String, Object> result = controller.ajaxSamplesMerge(TestDataFactory.PROJECT_ID, 1L, sampleIds, newName, Locale.US);

		// Ensure that the merge was requested
		verify(sampleService, times(1)).mergeSamples(any(Project.class), any(Sample.class), any());

		// Ensure that the rename was not requested
		ArgumentCaptor<Sample> captor = ArgumentCaptor.forClass(Sample.class);
		verify(sampleService, times(1)).update(captor.capture());
		assertEquals(newName,captor.getValue().getSampleName(), "Sample name should be set");
		assertEquals(result.get("result"), "success", "Result is success");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetAjaxProjectSampleModels() {
		Sample sample = TestDataFactory.constructSample();
		when(projectService.read(anyLong())).thenReturn(project);
		when(sampleService.getSamplesForProject(any(Project.class))).thenReturn(ImmutableList.of(
				new ProjectSampleJoin(project, sample, true)
		));

		when(sampleService
				.getFilteredSamplesForProjects(any(List.class), any(List.class), any(String.class), isNull(), any(String.class), isNull(), isNull(),
						any(Integer.class), any(Integer.class), any(
								Sort.class)))
				.thenReturn(TestDataFactory.getPageOfProjectSampleJoin());
		DataTablesParams params = mock(DataTablesParams.class);
		when(params.getSort()).thenReturn(Sort.by(Direction.ASC, "sample.sampleName"));
		DataTablesResponse response = controller
				.getProjectSamples(1L, params, ImmutableList.of(), ImmutableList.of(), new UISampleFilter(), Locale.US);
		List<DataTablesResponseModel> data = response.getData();
		assertEquals(1, data.size(), "Has the correct number of samples");
		DTProjectSamples sampleData = (DTProjectSamples) data.get(0);
		assertEquals("Joined Sample", sampleData.getSampleName(), "Has the correct sample");

	}

	@Test
	public void testDownloadSamples() throws IOException {
		Project project = TestDataFactory.constructProject();
		Sample sample = TestDataFactory.constructSample();
		MockHttpServletResponse response = new MockHttpServletResponse();

		Path path = Paths.get(FILE_PATH);
		SequenceFile file = new SequenceFile(path);

		ImmutableList<SampleSequencingObjectJoin> filejoin = ImmutableList.of(new SampleSequencingObjectJoin(sample,
				new SingleEndSequenceFile(file)));

		when(projectService.read(project.getId())).thenReturn(project);
		when(sampleService.readMultiple(ImmutableList.of(sample.getId()))).thenReturn(ImmutableList.of(sample));
		when(sequencingObjectService.getSequencingObjectsForSample(sample)).thenReturn(filejoin);

		controller.downloadSamples(project.getId(), ImmutableList.of(sample.getId()), response);

		verify(projectService).read(project.getId());
		verify(sampleService).readMultiple(ImmutableList.of(sample.getId()));
		verify(sequencingObjectService).getSequencingObjectsForSample(sample);

		assertTrue(response.containsHeader("Content-Disposition"),
				"Response should contain a \"Content-Disposition\" header.");
		assertEquals("attachment; filename=\"test_project.zip\"", response.getHeader("Content-Disposition"),
				"Content-Disposition should include the file name");

		try (ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(response.getContentAsByteArray()))) {
			ZipEntry nextEntry = zipStream.getNextEntry();
			String fileName = nextEntry.getName();
			assertTrue(fileName.endsWith(file.getFileName()), "incorrect file in zip stream: " + file.getFileName());
		}
	}

	@Test
	public void testDownloadSamplesWithSameName() throws IOException {
		Project project = TestDataFactory.constructProject();
		Sample sample = TestDataFactory.constructSample();
		MockHttpServletResponse response = new MockHttpServletResponse();

		Path path = Paths.get(FILE_PATH);
		SequenceFile file = new SequenceFile(path);

		ImmutableList<SampleSequencingObjectJoin> filejoin = ImmutableList.of(new SampleSequencingObjectJoin(sample,
				new SingleEndSequenceFile(file)), new SampleSequencingObjectJoin(sample,
				new SingleEndSequenceFile(file)), new SampleSequencingObjectJoin(sample,
				new SingleEndSequenceFile(file)));

		when(projectService.read(project.getId())).thenReturn(project);
		when(sampleService.readMultiple(ImmutableList.of(sample.getId()))).thenReturn(ImmutableList.of(sample));
		when(sequencingObjectService.getSequencingObjectsForSample(sample)).thenReturn(filejoin);

		controller.downloadSamples(project.getId(), ImmutableList.of(sample.getId()), response);

		verify(projectService).read(project.getId());
		verify(sampleService).readMultiple(ImmutableList.of(sample.getId()));
		verify(sequencingObjectService).getSequencingObjectsForSample(sample);

		assertTrue(response.containsHeader("Content-Disposition"),
				"Response should contain a \"Content-Disposition\" header.");
		assertEquals("attachment; filename=\"test_project.zip\"", response.getHeader("Content-Disposition"),
				"Content-Disposition should include the file name");

		try (ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(response.getContentAsByteArray()))) {
			Set<String> names = new HashSet<>();

			ZipEntry nextEntry = zipStream.getNextEntry();
			while (nextEntry != null) {
				names.add(nextEntry.getName());
				nextEntry = zipStream.getNextEntry();
			}

			assertEquals(3, names.size(), "should be 3 unique filenames");
		}

	}
}
