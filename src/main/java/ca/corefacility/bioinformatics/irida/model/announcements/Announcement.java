package ca.corefacility.bioinformatics.irida.model.announcements;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.user.User;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.GenerationType;
import javax.persistence.TemporalType;

import java.util.Date;
import java.util.List;

/**
 * An announcement object. Announcements can be created only by admin users, and announcements are displayed on the
 * dashboard page.
 */
@Entity
@Table(name = "announcement")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class Announcement implements IridaThing, Comparable<Announcement> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", updatable = false)
	private Date createdDate;

	@Column(name = "title")
	@NotNull
	private String title;

	@Column(name = "message")
	@Lob
	private String message;

	@Column(name = "priority")
	@NotNull
	private boolean priority;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "created_by_id")
	private User user;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "announcement")
	private List<AnnouncementUserJoin> users;

	/**
	 * Default constructor, needed by Hibernate.
	 */
	private Announcement() {
		createdDate = new Date();
		this.title = null;
		this.message = null;
		this.priority = false;
		this.user = null;
	}

	/**
	 * Create a new {@link Announcement} object, for display on the front page.
	 * 
	 * @param title    of the announcement
	 * @param message  Content of the announcement
	 * @param priority of the announcement
	 * @param user     The {@link User} that created the announcement
	 */
	public Announcement(String title, String message, boolean priority, User user) {
		this();
		this.title = title;
		this.message = message;
		this.priority = priority;
		this.user = user;
	}

	/**
	 * Create a new {@link Announcement} object with a created date, for testing purposes.
	 * 
	 * @param title       of the announcement
	 * @param message     Content of the announcement
	 * @param priority    of the announcement
	 * @param user        The {@link User} that created the announcement
	 * @param createdDate of the announcement
	 */
	public Announcement(String title, String message, boolean priority, User user, Date createdDate) {
		this.title = title;
		this.message = message;
		this.priority = priority;
		this.user = user;
		this.createdDate = createdDate;
	}

	@Override
	public int compareTo(Announcement other) {
		return ComparisonChain.start()
				.compareTrueFirst(priority, other.getPriority())
				.compare(other.getCreatedDate(), createdDate)
				.result();
	}

	public Long getId() {
		return id;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}

	public boolean getPriority() {
		return priority;
	}

	public User getUser() {
		return user;
	}

	public String getLabel() {
		return getMessage();
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setPriority(boolean priority) {
		this.priority = priority;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
