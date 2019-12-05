package ca.corefacility.bioinformatics.irida.model;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.persistence.MappedSuperclass;

import org.hibernate.HibernateException;
import org.hibernate.annotations.TypeDef;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.StringType;
import org.hibernate.usertype.UserType;

import com.google.common.base.Objects;

/**
 * Converts a {@link Path} to a {@link String} before persisting a value to a
 * column in a database. Allows us to store a the absolute path to a file
 * instead of trying to store the file in the relational database.
 * 
 * This implementation makes me very sad. It's a bit of a hack:
 * <ol>
 * <li>We're annotating the class with {@code MappedSuperclass} so that
 * hibernate will look at it and say "Hey, I need to manage this."</li>
 * <li>We're annotating the class with {@code TypeDef} so that we can tell
 * hibernate: "use this class to convert a {@code Path} to some sort of
 * database column."</li>
 * </ol>
 * 
 * This is ugly because we're using Hibernate-specific implementations of stuff.
 * Ideally, we'd use the {@code @Converter} stuff that comes with JPA2.1, but
 * Hibernate Envers barfs when you use the JPA converter annotations. See:
 * https://hibernate.atlassian.net/browse/HHH-9042
 * 
 * I e-mailed the author of the bug report because he mentioned that he had a
 * workaround. If he gets back to me, I will create another merge request to
 * remove this class and replace with with a much more sane JPA-compliant
 * version.
 * 
 *
 */
@MappedSuperclass
@TypeDef(defaultForType = Path.class, typeClass = PathConverter.class)
public class PathConverter implements UserType {

	@Override
	public int[] sqlTypes() {
		return new int[] { Types.VARCHAR };
	}

	@Override
	public Class<?> returnedClass() {
		return Path.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return Objects.equal(x, y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x == null ? 0 : x.hashCode();
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
			throws HibernateException, SQLException {
		String s = StringType.INSTANCE.nullSafeGet(rs, names[0], session);
		if(s == null){
			return null;
		}
		return Paths.get(s);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
			throws HibernateException, SQLException {
		String path = value == null ? null : value.toString();
		StringType.INSTANCE.nullSafeSet(st, path, index, session);
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return (Serializable) cached;
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}

}
