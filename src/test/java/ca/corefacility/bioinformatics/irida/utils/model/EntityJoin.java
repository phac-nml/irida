
package ca.corefacility.bioinformatics.irida.utils.model;

import ca.corefacility.bioinformatics.irida.model.joins.Join;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;

/**
 *
 */
@Entity
@Table(name="entityjoin")
@Audited
public class EntityJoin implements Join<IdentifiableTestEntity, OtherEntity>{
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.DETACH)
    @JoinColumn(name="identifiableTestEntity_id")
    private IdentifiableTestEntity identifiableTestEntity;
    
	@ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.DETACH)    
	@JoinColumn(name="otherEntity_id")
    private OtherEntity otherEntity;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
	
	public EntityJoin(){}
	
	public Long getId(){
		return id;
	}
	
	public void setId(Long id){
		this.id = id;
	}
	
	@Override
	public IdentifiableTestEntity getSubject() {
		return identifiableTestEntity;
	}

	@Override
	public OtherEntity getObject() {
		return otherEntity;	
	}

	@Override
	public Date getTimestamp() {
		return getCreatedDate();
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}
}
