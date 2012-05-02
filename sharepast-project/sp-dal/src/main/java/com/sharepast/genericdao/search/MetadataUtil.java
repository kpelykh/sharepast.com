package com.sharepast.genericdao.search;

import java.io.Serializable;

/**
 * The framework uses an implementation of this interface to introspect the
 * objects and relationships maintained by the JPA provider.
 * 
 * This interface provides a layer of abstraction between the framework and the
 * underlying ORM or JPA provider (ex. Hibernate). By switching out the
 * implementation of this interface, the framework should be able to be used
 * with different JPA providers.
 * 
 * @author dwolverton
 */
public interface MetadataUtil {
	/**
	 * Get the value of the ID property of an entity.
	 */
	public Serializable getId(Object object);

	/**
	 * Return true if the property at the given property path is the id of some
	 * entity.
	 */
	public boolean isId(Class<?> rootClass, String propertyPath);

	/**
	 * Get the Metadata for an entity class.
	 * 
	 * @throws IllegalArgumentException
	 *             if the class is not a Hibernate entity.
	 */
	public Metadata get(Class<?> klass) throws IllegalArgumentException;

	/**
	 * Get the Metadata for a property of an entity class. The property can be
	 * simple ("name") or nested ("organization.name").
	 * 
	 * @throws IllegalArgumentException
	 *             if the root class is not a Hibernate entity.
	 * @throws IllegalArgumentException
	 *             if the class does not have the given property.
	 */
	public Metadata get(Class<?> rootEntityClass, String propertyPath) throws IllegalArgumentException;
	
	/**
	 * Return the actual entity class registered with the persistence provider.
	 * This may be the same class or it may be different if the given class is
	 * is a proxy. For example, the entity class may be Person, but the class
	 * of the proxy may be Person_$$_javassist_5. We need to normalize this to
	 * Person so that we can create correct queries and inspect metadata correctly.
	 */
	public <T> Class<T> getUnproxiedClass(Class<?> klass);
	
	/**
	 * Return the actual entity class registered with the persistence provider.
	 * This may be the same as entity.getClass() or it may be different if the object is
	 * is a proxy. For example, the entity class may be Person, but the class
	 * of the proxy object may be Person_$$_javassist_5. We need to normalize this to
	 * Person so that we can create correct queries and inspect metadata correctly.
	 */
	public <T> Class<T> getUnproxiedClass(Object entity);
}
