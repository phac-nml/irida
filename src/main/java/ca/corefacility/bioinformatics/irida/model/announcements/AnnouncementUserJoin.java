package ca.corefacility.bioinformatics.irida.model.announcements;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.user.User;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.GenerationType;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * {@link AnnouncementUserJoin} object
 *
 * This is created whenever a {@link User} has confirmed that they have read an {@link Announcement}
 */

@Entity
@Table(name = "announcement_user", uniqueConstraints = @UniqueConstraint(columnNames = {"announcement_id", "user_id"}))
@Audited
@EntityListeners(AuditingEntityListener.class)
public class AnnouncementUserJoin implements Join<Announcement, User>, Comparable<AnnouncementUserJoin> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", updatable = false)
	private Date createdDate;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "announcement_id")
	private Announcement announcement;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "user_id")
	private User user;

	public AnnouncementUserJoin() {
		createdDate = new Date();
	}

	public AnnouncementUserJoin(Announcement announcement, User user) {
		this();
		this.announcement = announcement;
		this.user = user;
	}

	@Override
	public int compareTo(AnnouncementUserJoin other) {
		return announcement.compareTo(other.getSubject());
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getTimestamp() {
		return getCreatedDate();
	}

	public Long getId() {
		return id;
	}

	public Announcement getSubject() {
		return announcement;
	}

	public User getObject() {
		return user;
	}

}
