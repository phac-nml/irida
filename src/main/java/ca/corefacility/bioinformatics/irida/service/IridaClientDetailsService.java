package ca.corefacility.bioinformatics.irida.service;

import org.springframework.security.oauth2.provider.ClientDetailsService;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;


public interface IridaClientDetailsService extends CRUDService<Long, IridaClientDetails>, ClientDetailsService{

}
