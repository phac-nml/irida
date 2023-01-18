package ca.corefacility.bioinformatics.irida.web.controller.test.unit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Generates test data for unit tests.
 */
public final class TestDataFactory {
	public static final long TEST_PROJECT_ID = 1L;
	public static final String TEST_PROJECT_LABEL = "Test Project";
	public static final String TEST_PROJECT_ORGANISM = "Escherichia";
	public static final String TEST_PROJECT_DESCRIPTION = "This is a fascinating project";

	/**
	 * Construct a simple {@link ca.corefacility.bioinformatics.irida.model.User}.
	 *
	 * @return a {@link ca.corefacility.bioinformatics.irida.model.User} with identifier.
	 */
	public static User constructUser() {
		User u = new User();
		String username = "fbristow";
		u.setId(1L);
		u.setUsername(username);

		return u;
	}

	/**
	 * Construct a simple {@link ca.corefacility.bioinformatics.irida.model.sample.Sample}.
	 *
	 * @return a sample with a name and identifier.
	 */
	public static Sample constructSample() {
		String sampleName = "sampleName";
		Sample s = new Sample();
		s.setSampleName(sampleName);
		s.setId(1L);
		return s;
	}

	/**
	 * Construct a simple {@link ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile}.
	 *
	 * @return a {@link ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile} with identifier.
	 */
	public static SequenceFile constructSequenceFile() throws IOException {
		Path f = Files.createTempFile(null, null);
		SequenceFile sf = new SequenceFile();
		sf.setId(1L);
		sf.setFile(f);
		return sf;
	}

	/**
	 * Construct a simple {@link SingleEndSequenceFile}
	 * 
	 * @return a {@link SingleEndSequenceFile} with a {@link SequenceFile} and id
	 * @throws IOException if the temp file couldn't be created
	 */
	public static SingleEndSequenceFile constructSingleEndSequenceFile() throws IOException {
		SequenceFile sf = constructSequenceFile();
		SingleEndSequenceFile sesf = new SingleEndSequenceFile(sf);
		sesf.setId(2L);
		return sesf;
	}

	/**
	 * Construct a simple {@link SequenceFilePair} object
	 * 
	 * @return a {@link SequenceFilePair}
	 * @throws IOException if the temp files couldn't be created
	 */
	public static SequenceFilePair constructSequenceFilePair() throws IOException {
		SequenceFile sf1 = constructSequenceFile();
		SequenceFile sf2 = constructSequenceFile();

		SequenceFilePair pair = new SequenceFilePair(sf1, sf2);
		pair.setId(1L);

		return pair;
	}

	/**
	 * Construct a simple {@link ca.corefacility.bioinformatics.irida.model.Project}.
	 *
	 * @return a project with a name and identifier.
	 */
	public static Project constructProject() {
		Project p = new Project(TEST_PROJECT_LABEL);
		p.setId(TEST_PROJECT_ID);
		p.setOrganism(TEST_PROJECT_ORGANISM);
		p.setProjectDescription(TEST_PROJECT_DESCRIPTION);
		return p;
	}
}
