
package ca.corefacility.bioinformatics.irida.model.joins.impl;

import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
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
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Entity
@Table(name="miseqrun_sequencefile")
@Audited
public class MiseqRunSequenceFileJoin implements Join<MiseqRun, SequenceFile>{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    
    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.DETACH)
    @JoinColumn(name="miseqRun_id")
    private MiseqRun miseqRun;
    
	@ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.DETACH)    
	@JoinColumn(name="sequenceFile_id")
    private SequenceFile sequenceFile;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    
    public MiseqRunSequenceFileJoin(){
        createdDate = new Date();
    }
    
    public MiseqRunSequenceFileJoin(MiseqRun subject, SequenceFile object){
        this();
        this.miseqRun=subject;
        this.sequenceFile=object;
    }
    @Override
    public MiseqRun getSubject() {
        return miseqRun;
    }

    @Override
    public void setSubject(MiseqRun subject) {
        this.miseqRun = subject;
    }

    @Override
    public SequenceFile getObject() {
        return sequenceFile;
    }

    @Override
    public void setObject(SequenceFile object) {
        this.sequenceFile = object;
    }

    @Override
    public Date getTimestamp() {
        return createdDate;
    }

    @Override
    public void setTimestamp(Date timestamp) {
        this.createdDate = timestamp;
    }

}
