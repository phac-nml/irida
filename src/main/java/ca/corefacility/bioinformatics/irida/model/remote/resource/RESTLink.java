package ca.corefacility.bioinformatics.irida.model.remote.resource;

/**
 * Link object for elements retrieved from an IRIDA REST API
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class RESTLink {
	private String rel;
	private String href;
	
	public RESTLink(){
		
	}

	public RESTLink(String rel, String href) {
		this.rel = rel;
		this.href = href;
	}

	/**
	 * @return the rel
	 */
	public String getRel() {
		return rel;
	}

	/**
	 * @param rel
	 *            the rel to set
	 */
	public void setRel(String rel) {
		this.rel = rel;
	}

	/**
	 * @return the href
	 */
	public String getHref() {
		return href;
	}

	/**
	 * @param href
	 *            the href to set
	 */
	public void setHref(String href) {
		this.href = href;
	}

}
