package ca.corefacility.bioinformatics.irida.model.subscription;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Class for storing email subscriptions to {@link Project}s for {@link User}s .
 */
@Entity
@Table(name = "project_subscription", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id",
        "project_id"}, name = "UK_PROJECT_SUBSCRIPTION_PROJECT_USER"))
@Audited
@EntityListeners(AuditingEntityListener.class)
public class ProjectSubscription implements IridaThing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PROJECT_SUBSCRIPTION_USER"))
    @NotNull
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinColumn(name = "project_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PROJECT_SUBSCRIPTION_PROJECT"))
    @NotNull
    private Project project;

    @NotNull
    @Column(name = "email_subscription")
    private boolean emailSubscription;

    @CreatedDate
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", updatable = false)
    private Date createdDate;

    public ProjectSubscription() {
    }

    public ProjectSubscription(User user, Project project, boolean emailSubscription) {
        super();
        this.user = user;
        this.project = project;
        this.emailSubscription = emailSubscription;
        this.createdDate = new Date();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return "Project Subscription " + id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public boolean isEmailSubscription() {
        return emailSubscription;
    }

    public void setEmailSubscription(boolean emailSubscription) {
        this.emailSubscription = emailSubscription;
    }

    @Override
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}