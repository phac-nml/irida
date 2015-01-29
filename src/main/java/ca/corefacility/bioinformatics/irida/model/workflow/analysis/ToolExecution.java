package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import ca.corefacility.bioinformatics.irida.model.IridaThing;

/**
 * A historical record of how a tool was executed by a workflow execution
 * manager to produce some set of outputs.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "tool_execution")
@EntityListeners(AuditingEntityListener.class)
public class ToolExecution implements IridaThing {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

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

	// TODO: add command-line requirement back *after* Galaxy and blend4j have
	// been updated.
	// @NotNull
	@Lob
	@Column(name = "command_line")
	private String commandLine;

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

	private ToolExecution(final Long id, final Set<ToolExecution> previousSteps, final String toolName,
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
		if (executionTimeParameters == null) {
			this.executionTimeParameters = new HashMap<>();
		} else {
			this.executionTimeParameters = executionTimeParameters;
		}
		this.createdDate = new Date();
	}

	public final void addPreviousStep(final ToolExecution toolExecution) {
		this.previousSteps.add(toolExecution);
	}

	public final Set<ToolExecution> getPreviousSteps() {
		return ImmutableSet.copyOf(previousSteps);
	}

	public final String getToolName() {
		return toolName;
	}

	public final String getToolVersion() {
		return toolVersion;
	}

	public final String getCommandLine() {
		return commandLine;
	}

	public final Object getExecutionManagerIdentifier() {
		return executionManagerIdentifier;
	}

	public final Map<String, String> getExecutionTimeParameters() {
		return ImmutableMap.copyOf(executionTimeParameters);
	}

	public final void addExecutionTimeParameter(final String paramName, final String paramValue) {
		this.executionTimeParameters.put(paramName, paramValue);
	}

	public final void addExecutionTimeParameters(final Map<String, String> parameters) {
		this.executionTimeParameters.putAll(parameters);
	}

	@Override
	public Date getCreatedDate() {
		return this.createdDate;
	}

	@Override
	public Date getModifiedDate() {
		return this.createdDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		throw new UnsupportedOperationException("ToolExecution cannot be modified.");
	}

	@Override
	public String getLabel() {
		return this.toolName;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
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
