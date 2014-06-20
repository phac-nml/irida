package ca.corefacility.bioinformatics.irida.model.run;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

@Entity
@Table(name="iontorrent_run")
@Audited
public class IonTorrentRun extends SequencingRun{
	private String flowcell;
	
	public IonTorrentRun() {
		super();
	}
	
	public String getFlowcell(){
		return flowcell;
	}
	
	public void setFlowcell(String flowcell){
		this.flowcell = flowcell;
	}
	
	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

}
