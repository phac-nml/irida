package ca.corefacility.bioinformatics.irida.ria.web.models.tables;

import org.springframework.data.domain.Sort;

/**
 * Class to represent a single column sort in a AntD Table Request.
 */
public class AntSort {
	private String property;
	private String direction;

	public AntSort(String property, String direction) {
		this.property = property;
		this.direction = direction;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public Sort.Order getOrder() {
		return new Sort.Order(Sort.Direction.fromString(direction), property);
	}
}
