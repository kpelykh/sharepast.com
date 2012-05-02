package java.com.sharepast.genericdao.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.CollectionType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

import java.io.Serializable;


/**
 * Implementation of Metadata for a Hibernate entity.
 *
 * http://code.google.com/p/hibernate-generic-dao/
 */
public class HibernateEntityMetadata implements Metadata {

	private SessionFactory sessionFactory;
	private ClassMetadata metadata;
	private Class<?> collectionType;
	
	public HibernateEntityMetadata(SessionFactory sessionFactory, ClassMetadata classMetadata, Class<?> collectionType) {
		this.sessionFactory = sessionFactory;
		this.metadata = classMetadata;
		this.collectionType = collectionType;
	}
	
	public String getEntityName() {
		return metadata.getEntityName();
	}
	
	public String getIdProperty() {
		return metadata.getIdentifierPropertyName();
	}

	public Metadata getIdType() {
		return new HibernateNonEntityMetadata(sessionFactory, metadata.getIdentifierType(), null);
	}

	public Serializable getIdValue(Object object) {
		if (object instanceof HibernateProxy) {
			return ((HibernateProxy) object).getHibernateLazyInitializer().getIdentifier();
		} else {
			return metadata.getIdentifier(object, (SessionImplementor) sessionFactory.getCurrentSession());
		}
	}

	public Class<?> getJavaClass() {
		return metadata.getMappedClass();
	}

	public String[] getProperties() {
		String[] pn = metadata.getPropertyNames();
		String[] result = new String[pn.length + 1];
		result[0] = metadata.getIdentifierPropertyName();
		for (int i = 0; i < pn.length; i++) {
			result[i+1] = pn[i];
		}
		return result;
	}

	public Metadata getPropertyType(String property) {
		Type pType = metadata.getPropertyType(property);
		Class<?> pCollectionType = null;
		if (pType.isCollectionType()) {
			pType = ((CollectionType)pType).getElementType((SessionFactoryImplementor) sessionFactory);
			pCollectionType = pType.getReturnedClass();
		}
		
		if (pType.isEntityType()) {
			return new HibernateEntityMetadata(sessionFactory, sessionFactory.getClassMetadata(((EntityType)pType).getName()), pCollectionType);
		} else {
			return new HibernateNonEntityMetadata(sessionFactory, pType, pCollectionType);
		}
	}

	public Object getPropertyValue(Object object, String property) {
		if (getIdProperty().equals(property))
			return metadata.getIdentifier(object, (SessionImplementor) sessionFactory.getCurrentSession());
		else
			return metadata.getPropertyValue(object, property);
	}

	public boolean isCollection() {
		return collectionType != null;
	}

	public Class<?> getCollectionClass() {
		return collectionType;
	}

	public boolean isEmeddable() {
		return false;
	}

	public boolean isEntity() {
		return true;
	}

	public boolean isNumeric() {
		return false;
	}

	public boolean isString() {
		return false;
	}

}
