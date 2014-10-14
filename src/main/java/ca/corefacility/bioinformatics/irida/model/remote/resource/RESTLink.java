package ca.corefacility.bioinformatics.irida.model.remote.resource;

import java.util.Objects;

/**
 * Link object for elements retrieved from an IRIDA REST API
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class RESTLink {
	private String rel;
	private String href;

	public RESTLink() {

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

	@Override
	public int hashCode() {
		return Objects.hash(rel, href);
	}

	@Override
	public String toString() {
		return "RESTLink[ " + rel + " => " + href + " ]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RESTLink) {
			RESTLink other = (RESTLink) obj;
			return Objects.equals(rel, other.rel) && Objects.equals(href, other.href);
		}
		return false;

	}

}
