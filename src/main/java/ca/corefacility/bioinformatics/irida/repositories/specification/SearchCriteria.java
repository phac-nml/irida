package ca.corefacility.bioinformatics.irida.repositories.specification;

/**
 * Define a search criteria for a given key and value. To be used with Specification classes.
 */
public class SearchCriteria {
	private String key;
	private Object value;
	private SearchOperation operation;

	public SearchCriteria() {

	}

	public SearchCriteria(String key, Object value, SearchOperation operation) {
		this.key = key;
		this.value = value;
		this.operation = operation;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setOperation(SearchOperation operation) {
		this.operation = operation;
	}

	public String getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	public SearchOperation getOperation() {
		return operation;
	}
}
