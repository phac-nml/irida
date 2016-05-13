package ca.corefacility.bioinformatics.irida.model.announcements;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "announcements_user")
//@Audited
//@EntityListeners(AuditingEntityListener.class)
public class AnnouncementsUser {

    @Id
    private Long id;

    @CreatedDate
    @Column(name = "date_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

//    @OneToOne
//    @JoinColumn(name = "id", table = "announcement")
    private Long announcementId;

//    @ManyToOne
//    @JoinColumn(name = "id", table = "user")
    private Long userId;

    public Date getDateCreated() {
        return dateCreated;
    }

    public Long getAnnouncementId() {
        return announcementId;
    }

    public Long getUserId() {
        return userId;
    }

}
