package ca.corefacility.bioinformatics.irida.model.announcements;


import ca.corefacility.bioinformatics.irida.model.IridaThing;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;


@Entity
@Table(name = "announcement")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class Announcement implements IridaThing {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @CreatedDate
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private final Date createdDate;

    @Column(name = "message")
    @Lob
    private String message;

    @Column(name = "created_by_id")
    private Long createdById;

    public Announcement() {
        createdDate = new Date();
    }

    public Announcement(String message) {
        this();
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public String getMessage() {
        return message;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public String getLabel() {
        return getMessage();
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreatedById(Long id) {
        this.createdById = id;
    }

}
