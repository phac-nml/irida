package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaThing;

import com.google.common.collect.ImmutableSet;

/**
 * A historical record of how a tool was executed by a workflow execution
 * manager to produce some set of outputs.
 * 
 *
 */
@Entity
@Table(name = "tool_execution")
@EntityListeners(AuditingEntityListener.class)
public class ToolExecution implements IridaThing {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private final Long id;

	@NotNull
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "tool_execution_prev_steps", joinColumns = @JoinColumn(name = "tool_execution_id"), inverseJoinColumns = @JoinColumn(name = "tool_execution_prev_id"))
	private final Set<ToolExecution> previousSteps;

	@NotNull
	@Column(name = "tool_name")
	private final String toolName;

	@NotNull
	@Column(name = "tool_version")
	private final String toolVersion;

	@NotNull
	@Lob
	@Column(name = "command_line")
	private final String commandLine;

	@NotNull
	@Lob
	@Column(name = "execution_manager_identifier")
	private final String executionManagerIdentifier;

	@NotNull
	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "execution_parameter_key", nullable = false)
	@Column(name = "execution_parameter_value", nullable = false)
	@CollectionTable(name = "tool_execution_parameters", joinColumns = @JoinColumn(name = "tool_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"tool_id", "execution_parameter_key" }, name = "UK_TOOL_EXECUTION_PARAM_KEY"))
	private final Map<String, String> executionTimeParameters;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private final Date createdDate;

	/**
	 * for hibernate.
	 */
	@SuppressWarnings("unused")
	private ToolExecution() {
		this.id = null;
		this.previousSteps = null;
		this.toolName = null;
		this.toolVersion = null;
		this.commandLine = null;
		this.executionManagerIdentifier = null;
		this.executionTimeParameters = null;
		this.createdDate = null;
	}

	/**
	 * Construct a new instance of {@link ToolExecution}.
	 * 
	 * @param previousSteps
	 *            the set of {@link ToolExecution} that led to the input of this
	 *            {@link ToolExecution}.
	 * @param toolName
	 *            the name of the tool that was executed.
	 * @param toolVersion
	 *            the version of the tool that was executed.
	 * @param executionManagerIdentifier
	 *            the execution manager identifier that this provenance was
	 *            derived from (the history step in Galaxy).
	 * @param executionTimeParameters
	 *            the parameters that were passed to the tool at execution time.
	 * @param commandLine
	 *            the actual command line invocation that launched this tool.
	 */
	public ToolExecution(final Set<ToolExecution> previousSteps, final String toolName, final String toolVersion,
			final String executionManagerIdentifier, final Map<String, String> executionTimeParameters,
			final String commandLine) {
		this.id = null;
		this.toolName = toolName;
		this.toolVersion = toolVersion;
		this.executionManagerIdentifier = executionManagerIdentifier;

		if (previousSteps == null) {
			this.previousSteps = new HashSet<>();
		} else {
			this.previousSteps = previousSteps;
		}
		this.executionTimeParameters = addExecutionTimeParameters(executionTimeParameters);
		this.createdDate = new Date();
		this.commandLine = (commandLine == null) ? "null" : commandLine;
	}

	/**
	 * Construct a new instance of {@link ToolExecution}.
	 *
	 * @param id
	 *            the id for the ToolExecution
	 * @param previousSteps
	 *            the set of {@link ToolExecution} that led to the input of this
	 *            {@link ToolExecution}.
	 * @param toolName
	 *            the name of the tool that was executed.
	 * @param toolVersion
	 *            the version of the tool that was executed.
	 * @param executionManagerIdentifier
	 *            the execution manager identifier that this provenance was
	 *            derived from (the history step in Galaxy).
	 * @param executionTimeParameters
	 *            the parameters that were passed to the tool at execution time.
	 */
	public ToolExecution(final Long id, final Set<ToolExecution> previousSteps, final String toolName,
			final String toolVersion, final String executionManagerIdentifier,
			final Map<String, String> executionTimeParameters) {
		this.id = id;
		this.toolName = toolName;
		this.toolVersion = toolVersion;
		this.executionManagerIdentifier = executionManagerIdentifier;

		if (previousSteps == null) {
			this.previousSteps = new HashSet<>();
		} else {
			this.previousSteps = previousSteps;
		}
		this.executionTimeParameters = addExecutionTimeParameters(executionTimeParameters);
		this.createdDate = new Date();
		this.commandLine = null;
	}

	@Override
	public String toString() {
		return "ToolExecution [toolName=" + this.toolName + ", toolVersion=" + toolVersion + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(toolName, toolVersion, executionTimeParameters);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		} else if (o instanceof ToolExecution) {
			final ToolExecution t = (ToolExecution) o;
			return Objects.equals(toolName, t.toolName) && Objects.equals(toolVersion, t.toolVersion)
					&& Objects.equals(executionTimeParameters, t.executionTimeParameters);
		}

		return false;
	}

	public Set<ToolExecution> getPreviousSteps() {
		return ImmutableSet.copyOf(previousSteps);
	}

	public String getToolName() {
		return toolName;
	}

	public String getToolVersion() {
		return toolVersion;
	}

	public String getCommandLine() {
		return commandLine;
	}

	public String getExecutionManagerIdentifier() {
		return executionManagerIdentifier;
	}

	/**
	 * Get the parameters for execution time of a pipeline
	 *
	 * @return The unescaped execution time parameters
	 */
	public Map<String, String> getExecutionTimeParameters() {
		final Map<String, String> unescapedKeys = new HashMap<>();
		for (final Entry<String, String> param : executionTimeParameters.entrySet()) {
			final String unescapedKey = param.getKey().replaceAll("\\\\([A-Z])", "$1");
			unescapedKeys.put(unescapedKey, param.getValue());
		}
		return Collections.unmodifiableMap(unescapedKeys);
	}

	/**
	 * Set the execution time parameters
	 *
	 * @param parameters the parameters to set
	 * @return the escaped parameters
	 */
	private final Map<String, String> addExecutionTimeParameters(final Map<String, String> parameters) {
		final Map<String, String> escapedParameters = new HashMap<>(parameters.size());
		for (final Entry<String, String> param : parameters.entrySet()) {
			final String escapedKey = param.getKey().replaceAll("([A-Z])", "\\\\$1");
			escapedParameters.put(escapedKey, param.getValue());
		}
		return escapedParameters;
	}

	@Override
	public Date getCreatedDate() {
		return this.createdDate;
	}

	@Override
	public String getLabel() {
		return this.toolName;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	/**
	 * Indicates whether or not this tool is an input tool or data source.
	 * 
	 * @return true if the tool execution is an input tool, false otherwise.
	 */
	public boolean isInputTool() {
		return this.previousSteps.isEmpty();
	}
}
