package ca.corefacility.bioinformatics.irida.repositories.specification;

/**
 * Search operations available to use in SearchCriteria's
 */
public enum SearchOperation {
	GREATER_THAN("GREATER_THAN"),
	LESS_THAN("LESS_THAN"),
	GREATER_THAN_EQUAL("GREATER_THAN_EQUAL"),
	LESS_THAN_EQUAL("LESS_THAN_EQUAL"),
	NOT_EQUAL("NOT_EQUAL"),
	EQUAL("EQUAL"),
	MATCH("MATCH"),
	MATCH_START("MATCH_START"),
	MATCH_END("MATCH_END"),
	MATCH_IN("MATCH_IN"),
	IN("IN"),
	NOT_IN("NOT_IN");

	private String operation;

	private SearchOperation(String operation) {
		this.operation = operation;
	}

	@Override
	public String toString() {
		return operation;
	}

	/**
	 * Get a operation from the given string operation
	 * 
	 * @param operation the string to get the operation for
	 * @return the request SearchOperation
	 */
	public static SearchOperation fromString(String operation) {
		switch (operation.toUpperCase()) {
		case "GREATER_THAN":
			return GREATER_THAN;
		case "LESS_THAN":
			return LESS_THAN;
		case "GREATER_THAN_EQUAL":
			return GREATER_THAN_EQUAL;
		case "LESS_THAN_EQUAL":
			return LESS_THAN_EQUAL;
		case "NOT_EQUAL":
			return NOT_EQUAL;
		case "EQUAL":
			return EQUAL;
		case "MATCH":
			return MATCH;
		case "MATCH_START":
			return MATCH_START;
		case "MATCH_END":
			return MATCH_END;
		case "MATCH_IN":
			return MATCH_IN;
		case "IN":
			return IN;
		case "NOT_IN":
			return NOT_IN;
		default:
			return MATCH;
		}
	}
}
