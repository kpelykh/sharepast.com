package com.sharepast.genericdao.hibernate;

import com.sharepast.genericdao.DAOUtil;
import com.sharepast.genericdao.search.ISearch;
import com.sharepast.genericdao.search.Search;
import com.sharepast.genericdao.search.SearchResult;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of <code>GenericDAO</code> using Hibernate.
 * The SessionFactory property is annotated for automatic resource injection.
 *
 * http://code.google.com/p/hibernate-generic-dao/
 *
 * @param <T>
 *            The type of the domain object for which this instance is to be
 *            used.
 * @param <ID>
 *            The type of the id of the domain object for which this instance is
 *            to be used.
 */
public class GenericDAOImpl<T, ID extends Serializable> implements GenericDAO<T, ID> {

	protected Class<T> persistentClass = (Class<T>) DAOUtil.getTypeArguments(GenericDAOImpl.class, this.getClass()).get(0);

    // By replacing inheritance with delegation, we make DAO APISs much clearer, and do not mux API from
    // HibernateBaseDAO with GenericDAO interface
    @Autowired
    private HibernateBaseDAO hibernateBaseDao;
    
    public long count(ISearch search) {
        if (search == null)
            search = new Search();
        return hibernateBaseDao._count(persistentClass, search);
    }

    public long count() {
        return hibernateBaseDao._count(persistentClass, new Search());
    }

	public T find(ID id) {
		return hibernateBaseDao._get(persistentClass, id);
	}

	public T[] find(ID... ids) {
		return hibernateBaseDao._get(persistentClass, ids);
	}

	public List<T> findAll() {
		return hibernateBaseDao._all(persistentClass);
	}

	public void flush() {
        hibernateBaseDao._flush();
	}

	public T getReference(ID id) {
		return hibernateBaseDao._load(persistentClass, id);
	}

	public T[] getReferences(ID... ids) {
		return hibernateBaseDao._load(persistentClass, ids);
	}

	public boolean isAttached(T entity) {
		return hibernateBaseDao._sessionContains(entity);
	}

	public void refresh(T... entities) {
        hibernateBaseDao._refresh(entities);
	}

	public boolean remove(T entity) {
		return hibernateBaseDao._deleteEntity(entity);
	}

	public void remove(T... entities) {
        hibernateBaseDao._deleteEntities(entities);
	}

	public boolean removeById(ID id) {
		return hibernateBaseDao._deleteById(persistentClass, id);
	}

	public void removeByIds(ID... ids) {
        hibernateBaseDao._deleteById(persistentClass, ids);
	}

	public boolean save(T entity) {
		return hibernateBaseDao._saveOrUpdateIsNew(entity);
	}

	public boolean[] save(T... entities) {
		return hibernateBaseDao._saveOrUpdateIsNew(entities);
	}

    public <RT> List<RT> search(ISearch search) {
        if (search == null)
            return (List<RT>) findAll();
        return hibernateBaseDao._search(persistentClass, search);
    }

    public <RT> SearchResult<RT> searchAndCount(ISearch search) {
        if (search == null) {
            SearchResult<RT> result = new SearchResult<RT>();
            result.setResult((List<RT>) findAll());
            result.setTotalCount(result.getResult().size());
            return result;
        }
        return hibernateBaseDao._searchAndCount(persistentClass, search);
    }

    public <RT> RT searchUnique(ISearch search) {
        return (RT) hibernateBaseDao._searchUnique(persistentClass, search);
    }

    // following methods can be used to lookup any type of object, not only persistentClass
    // it gets handy when you don't want to create a new DAO for every single table

    public <T> T findByCriteria(Class<T> returnType, CriteriaDetails criteriaDetails) {
        Assert.notNull( returnType );
        Assert.notNull( criteriaDetails );
        Object obj = constructCriteria(returnType, criteriaDetails).uniqueResult();
        if (obj == null && returnType.getName().equals("java.lang.String")) {
            return returnType.cast(0);
        }
        return returnType.cast(constructCriteria(returnType, criteriaDetails).uniqueResult());
    }

    public <T> List<T> listByQuery(Class<T> returnType, QueryDetails queryDetails) {
        Assert.notNull( returnType );
        Assert.notNull( queryDetails );
        return Collections.checkedList(constructQuery(queryDetails).list(), returnType);
    }

    public Criteria constructCriteria(Class returnType, CriteriaDetails criteriaDetails) {
        Assert.notNull( criteriaDetails );
        Criteria criteria;
        criteria = (criteriaDetails.getAlias() == null) ? hibernateBaseDao.getSession().createCriteria(returnType) : hibernateBaseDao.getSession().createCriteria(returnType, criteriaDetails.getAlias());
        return criteriaDetails.completeCriteria(criteria);
    }

    public Query constructQuery(QueryDetails queryDetails) {
        Assert.notNull( queryDetails );
        return queryDetails.completeQuery(hibernateBaseDao.getSession().createQuery(queryDetails.getQueryString()).setCacheable(true));
    }

}