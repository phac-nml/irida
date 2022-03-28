package ca.corefacility.bioinformatics.irida.ria.web.models.tables;

import org.springframework.data.domain.Sort;

public class AntSort {
	private String property;
	private String direction;

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
