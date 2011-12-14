package kp.app.persistence.orm.hibernate;

import kp.app.persistence.Durable;
import kp.app.persistence.orm.ORMDao;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Konstantin Pelykh
 * Date: Dec 6, 2008
 * Time: 7:54:51 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class HibernateDao<D extends Durable<Long>> implements ORMDao<Long, D> {

    @Autowired
    protected SessionFactory sessionFactory;
    private Class<D> entityClass;

    @PostConstruct
    public void register() {
        entityClass = getManagedClass();
    }

    @Override
    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public Class<D> getEntityClass() {
        return entityClass;
    }

    public D get(Long id) {
        return get(getManagedClass(), id);
    }

    public D get(Class<D> durableClass, Long id) {
        D durable;
        Object persistedObject;
        if ((persistedObject = getSession().get(durableClass, id)) != null) {
            durable = durableClass.cast(persistedObject);
            return durable;
        }
        return null;
    }

    @Override
    public D persist(D durable) {
        return persist(getManagedClass(), durable);
    }

    public D persist(Class<D> durableClass, D durable) {

        beforePersist(durable);

        D persistentDurable;

        if (getSession().contains(durable)) {
            persistentDurable = durable;
        } else {
            persistentDurable = getManagedClass().cast(getSession().merge(durable));
        }
        return persistentDurable;
    }

    //override this method if you need to have something done (in your DAO) before the new object is stored (or placed into session proxy)
    public void beforePersist(D durable) {
        //do nothing;
    }

    @Override
    public void delete(D durable) {
        delete(getManagedClass(), durable);
    }


    public void delete(Class<D> durableClass, D durable) {

        beforeDelete(durable);

        if (!getSession().contains(durable)) {
            getSession().delete(getSession().load(durable.getClass(), durable.getId()));
        } else {
            getSession().delete(durable);
        }
    }

    //override this method if you need to have something done (in your DAO) before the object is deleted (or placed into session proxy)
    public void beforeDelete(D durable) {
        //do nothing;
    }


    @Override
    public List<D> list() {
        return Collections.checkedList(getSession().createCriteria(getManagedClass()).list(), getManagedClass());
    }

    public Iterable<D> scroll(int fetchSize) {
        return new ScrollIterator<D>(getSession().createCriteria(getManagedClass()).setFetchSize(fetchSize).scroll(ScrollMode.SCROLL_INSENSITIVE), getManagedClass());
    }

    public long size() {
        return findByCriteria(Integer.class, new CriteriaDetails() {
            @Override
            public Criteria completeCriteria(Criteria criteria) {

                return criteria.setProjection(Projections.rowCount());
            }
        });
    }

    public <T> T findByCriteria(Class<T> returnType, CriteriaDetails criteriaDetails) {
        Object obj = constructCriteria(criteriaDetails).uniqueResult();
        if (obj == null && returnType.getName().equals("java.lang.String")) {
            return returnType.cast(0);
        }
        return returnType.cast(constructCriteria(criteriaDetails).uniqueResult());
    }

    public D findByCriteria(CriteriaDetails criteriaDetails) {
        return getManagedClass().cast(constructCriteria(criteriaDetails).uniqueResult());
    }

    public List<D> listByCriteria(CriteriaDetails criteriaDetails) {
        return Collections.checkedList(constructCriteria(criteriaDetails).list(), getManagedClass());
    }

    public <T> List<T> listByQuery (Class<T> returnType, QueryDetails queryDetails) {

         return Collections.checkedList(constructQuery(queryDetails).list(), returnType);
    }

    public List<D> listByQuery (QueryDetails queryDetails) {

        return Collections.checkedList(constructQuery(queryDetails).list(), getManagedClass());
    }

    public Criteria constructCriteria(CriteriaDetails criteriaDetails) {
        Criteria criteria;
        criteria = (criteriaDetails.getAlias() == null) ? getSession().createCriteria(getManagedClass()) : getSession().createCriteria(getManagedClass(), criteriaDetails.getAlias());
        return criteriaDetails.completeCriteria(criteria).setCacheable(true);
    }

    public Query constructQuery (QueryDetails queryDetails) {

        return queryDetails.completeQuery(getSession().createQuery(queryDetails.getQueryString()).setCacheable(true));
    }

    public Iterable<D> scrollByCriteria(CriteriaDetails criteriaDetails) {

        return new ScrollIterator<D>(constructCriteria(criteriaDetails).scroll(ScrollMode.SCROLL_INSENSITIVE), getManagedClass());
    }

}