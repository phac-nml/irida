package ca.corefacility.bioinformatics.irida.exceptions;
import java.util.regex.*;

/**
 * When an {@link Identifiable} entity to be created in the database shares an
 * identifier with an existing entity.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class EntityExistsException extends RuntimeException {

	private static final long serialVersionUID = 7353646703650984698L;
	private String fieldName;

	/**
     * Construct a new {@link EntityExistsException} with the specified message.
     *
     * @param message the message explaining the exception.
     */
    public EntityExistsException(String message) {
        super(message);
    }
	
	/**
     * Construct a new {@link EntityExistsException} with the specified message.
     *
     * @param message the message explaining the exception.
	 * @param fieldName The name of the field causing the exception
     */
    public EntityExistsException(String message, String fieldName) {
        this(message);
		this.fieldName = fieldName;
    }	

	/**
	 * Get the field name that caused the exception
	 * @return The name of the field that already exists in the database
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Set the field name that was in duplicate and caused the exception
	 * @param fieldName The field name causing the exception
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public static String parseConstraintName(String name, String tableName){
		
		String regex = "^"+tableName + "_(.*)_CONSTRAINT$";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(name);
		if(matcher.groupCount() != 1){
			throw new IllegalArgumentException("Couldn't parse field name from constraint violation: " + name);
		}
		
		matcher.find();
		String fieldName = matcher.group(1);
		
		return fieldName;
	}
}
