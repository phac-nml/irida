package ca.corefacility.bioinformatics.irida.service.impl;

import java.net.URI;
import java.net.URISyntaxException;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.repositories.remote.RemoteAPIRepository;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;

@Transactional
@Service
public class RemoteAPIServiceImpl extends CRUDServiceImpl<Long, RemoteAPI> implements RemoteAPIService{

	private RemoteAPIServiceImpl(){
		super(null,null,RemoteAPI.class);
	}
	
	@Autowired
	public RemoteAPIServiceImpl(RemoteAPIRepository repository, Validator validator) throws ConstraintViolationException, EntityExistsException, URISyntaxException {
		super(repository, validator, RemoteAPI.class);
	}
	
	@Override
	public RemoteAPI read(Long id) throws EntityNotFoundException {
		return super.read(id);
	}
	
	@Override
	public Iterable<RemoteAPI> findAll() {
		return super.findAll();
	}
	
	public void addStuff(){
		try {
			create(new RemoteAPI(new URI("http://localhost:8181"), "My local remote api"));
			create(new RemoteAPI(new URI("http://bobloblaw:8181"), "My bobloblaw remote api"));
		} catch (ConstraintViolationException | EntityExistsException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
