package ca.corefacility.bioinformatics.irida.model.workflow.submission;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

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
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaThing;

/**
 * Users can create, save, and re-use named parameters in their workflow
 * submissions.
 * 
 *
 */
@Entity
@Table(name = "workflow_named_parameters")
@EntityListeners(AuditingEntityListener.class)
public class IridaWorkflowNamedParameters implements IridaThing {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@NotNull
	@Size(min = 3)
	@Column(name = "name", nullable = false)
	private final String name;

	@NotNull
	@Column(name = "workflow_id", nullable = false)
	@Type(type = "uuid-char")
	private final UUID workflowId;

	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "named_parameter_name", nullable = false)
	@Column(name = "named_parameter_value", nullable = false)
	@CollectionTable(name = "workflow_named_parameter_values", joinColumns = @JoinColumn(name = "named_parameters_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"named_parameters_id", "named_parameter_name" }, name = "UK_WORKFLOW_PARAMETERS_NAME"))
	private final Map<String, String> namedParameters;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", nullable = false, updatable = false)
	private Date createdDate;

	/**
	 * For hibernate.
	 */
	@SuppressWarnings("unused")
	private IridaWorkflowNamedParameters() {
		this.createdDate = null;
		this.id = null;
		this.name = null;
		this.workflowId = null;
		this.namedParameters = null;
	}

	public IridaWorkflowNamedParameters(final String name, final UUID workflowId, final Map<String, String> parameters) {
		this.createdDate = new Date();
		this.workflowId = workflowId;
		this.name = name;
		this.namedParameters = parameters;
	}

	@Override
	public Date getCreatedDate() {
		return this.createdDate;
	}

	@Override
	public String getLabel() {
		return this.name;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public UUID getWorkflowId() {
		return workflowId;
	}

	public final Map<String, String> getInputParameters() {
		return namedParameters;
	}
}
