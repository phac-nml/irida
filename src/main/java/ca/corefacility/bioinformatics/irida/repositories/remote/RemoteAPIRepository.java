package ca.corefacility.bioinformatics.irida.repositories.remote;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;

@Component
public class RemoteAPIRepository {
	private static final Logger logger = LoggerFactory.getLogger(RemoteAPIRepository.class);

	Map<Long,RemoteAPI> store;
	Long maxid;
	
	public RemoteAPIRepository() throws URISyntaxException{
		store = new HashMap<>();
		maxid=0l;
		
		add(new RemoteAPI(new URI("http://localhost:8181"), "My local remote api"));
		add(new RemoteAPI(new URI("http://bobloblaw:8181"), "My bobloblaw remote api"));
	}
	
	public RemoteAPI read(Long id){
		logger.debug("Reading number " + id);
		return store.get(id);
	}
	
	public void add(RemoteAPI api){
		maxid++;
		logger.debug("Adding api " + api + " as id  " + maxid);
		api.setId(maxid);
		store.put(maxid, api);
	}
	
	public Collection<RemoteAPI> list(){
		return store.values();
	}
}
