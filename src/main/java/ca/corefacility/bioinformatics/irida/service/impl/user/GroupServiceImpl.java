package ca.corefacility.bioinformatics.irida.service.impl.user;

import java.util.Map;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.user.Group;
import ca.corefacility.bioinformatics.irida.repositories.user.GroupRepository;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.user.GroupService;

/**
 * Implementation of specialized service for managing {@link Group}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Service
public class GroupServiceImpl extends CRUDServiceImpl<Long, Group> implements GroupService {

	@Autowired
	public GroupServiceImpl(GroupRepository repository, Validator validator) {
		super(repository, validator, Group.class);
	}

	@Override
	public Group create(Group g) {
		return super.create(g);
	}
	
	@Override
	public Group update(Long id, Map<String, Object> updatedProperties) {
		return super.update(id, updatedProperties);
	}
}
