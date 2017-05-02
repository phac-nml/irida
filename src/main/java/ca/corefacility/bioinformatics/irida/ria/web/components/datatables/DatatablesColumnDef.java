package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import org.springframework.data.domain.Sort;

public class DatatablesColumnDef {
	private String name;
	private boolean sortable;
	private boolean sorted = false;
	private boolean searchable;
	private boolean filtered;
	private String regex;
	private String search;
	private String searchFrom;
	private String searchTo;
	private Sort.Direction sortDirection;
}
