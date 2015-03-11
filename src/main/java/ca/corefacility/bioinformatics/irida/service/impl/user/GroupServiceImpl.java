package ca.corefacility.bioinformatics.irida.service.impl.user;

import java.util.Map;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.user.Group;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.UserGroupJoin;
import ca.corefacility.bioinformatics.irida.repositories.joins.user.UserGroupJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.GroupRepository;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.user.GroupService;

/**
 * Implementation of specialized service for managing {@link Group}.
 * 
 *
 */
@Service
public class GroupServiceImpl extends CRUDServiceImpl<Long, Group> implements GroupService {

	private UserGroupJoinRepository userGroupRepository;

	@Autowired
	public GroupServiceImpl(GroupRepository repository, UserGroupJoinRepository userGroupRepository, Validator validator) {
		super(repository, validator, Group.class);
		this.userGroupRepository = userGroupRepository;
	}

	@Override
	public Group create(Group g) {
		return super.create(g);
	}

	@Override
	public Group update(Long id, Map<String, Object> updatedProperties) {
		return super.update(id, updatedProperties);
	}

	@Override
	public Join<User, Group> addUserToGroup(Group g, User u) throws EntityNotFoundException, EntityExistsException {
		try {
			UserGroupJoin ug = new UserGroupJoin(u, g);
			return userGroupRepository.save(ug);
		} catch (DataIntegrityViolationException e) {
			throw new EntityExistsException(String.format("The user [%s] already belongs to group [%s].", u.getId(),
					g.getId()));
		}
	}
}
