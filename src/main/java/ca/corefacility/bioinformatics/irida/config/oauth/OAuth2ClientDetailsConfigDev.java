package ca.corefacility.bioinformatics.irida.config.oauth;

import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

@Configuration
@Profile({"dev","test"})
public class OAuth2ClientDetailsConfigDev extends OAuth2ClientDetailsConfigProd implements OAuth2ClientDetailsConfig{
	
	@Override
	public Map<String, ClientDetails> clientDetailsList(){
		Map<String, ClientDetails> clientStore = super.clientDetailsList();
		
		BaseClientDetails testClient = new BaseClientDetails("testClient", "NmlIrida", "read,write", "password","ROLE_CLIENT");
		testClient.setClientSecret("testClientSecret");
		clientStore.put("testClient", testClient);
		
		BaseClientDetails webClientDetails = new BaseClientDetails("webClient", "NmlIrida", "read,write", "password,authorization_code","ROLE_CLIENT");
		webClientDetails.setClientSecret("webClientSecret");
		clientStore.put("webClient", webClientDetails);
	
		return clientStore;
	}
}
