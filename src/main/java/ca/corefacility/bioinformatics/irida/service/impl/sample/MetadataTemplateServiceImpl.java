package ca.corefacility.bioinformatics.irida.service.impl.sample;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataTemplateRepository;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;

@Service
public class MetadataTemplateServiceImpl extends CRUDServiceImpl<Long, MetadataTemplate>
		implements MetadataTemplateService {

	@Autowired
	public MetadataTemplateServiceImpl(MetadataTemplateRepository repository, Validator validator) {
		super(repository, validator, MetadataTemplate.class);
	}

}
