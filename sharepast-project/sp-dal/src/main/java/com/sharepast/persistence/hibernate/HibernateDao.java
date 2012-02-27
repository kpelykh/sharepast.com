package com.sharepast.persistence.hibernate;

import com.sharepast.domain.IEntity;
import com.sharepast.persistence.ORMDao;
import org.hibernate.*;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
@Scope( BeanDefinition.SCOPE_PROTOTYPE )
@Transactional( propagation = Propagation.MANDATORY)
public abstract class HibernateDao<D extends IEntity<Long>> implements ORMDao<D, Long> {

    @Autowired
    protected SessionFactory sessionFactory;

    private Class<D> entityClass;

    @PostConstruct
    private void register() {
        Assert.notNull(sessionFactory, "sessionFactory must not be null");
        entityClass = getEntityClass();
        Assert.notNull(entityClass, "entityClass must not be null");
    }

    protected final Session getCurrentSession(){
        return this.sessionFactory.getCurrentSession();
    }

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public abstract Class<D> getEntityClass();

    public D get(final Long id){
        Assert.notNull( id );
        return (D) this.getCurrentSession().get( getEntityClass(), id );
    }

    public List< D > list(){
        return Collections.checkedList(this.getCurrentSession()
                .createQuery("from " + getEntityClass().getName()).list(), getEntityClass());
    }

    public D persist(final D entity){
        Assert.notNull(entity);

        D persistentEntity;

        if (getSession().contains(entity)) {
            persistentEntity = entity;
        } else {
            persistentEntity = getEntityClass().cast(getSession().merge(entity));
        }
        return persistentEntity;
    }

    public void update(final D entity){
        Assert.notNull( entity );
        this.getCurrentSession().merge( entity );
    }

    public void delete(final D entity){
        Assert.notNull(entity);
        if (!getSession().contains(entity)) {
            getSession().delete(getSession().load(entity.getClass(), entity.getId()));
        } else {
            getSession().delete(entity);
        }
    }

    @Override
    public void deleteById(Long id) {
        Assert.notNull(id);
        final D entity = this.get(id);
        Assert.notNull( entity );
        this.delete( entity );
    }

    // ---------- Hibernate specific

    public Iterable<D> scroll(int fetchSize) {
        return new ScrollIterator<D>(getSession().createCriteria(getEntityClass()).setFetchSize(fetchSize).scroll(ScrollMode.SCROLL_INSENSITIVE), getEntityClass());
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
        Assert.notNull( returnType );
        Assert.notNull( criteriaDetails );
        Object obj = constructCriteria(criteriaDetails).uniqueResult();
        if (obj == null && returnType.getName().equals("java.lang.String")) {
            return returnType.cast(0);
        }
        return returnType.cast(constructCriteria(criteriaDetails).uniqueResult());
    }

    public D findByCriteria(CriteriaDetails criteriaDetails) {
        Assert.notNull(criteriaDetails);
        return getEntityClass().cast(constructCriteria(criteriaDetails).uniqueResult());
    }

    public List<D> listByCriteria(CriteriaDetails criteriaDetails) {
        Assert.notNull( criteriaDetails );
        return Collections.checkedList(constructCriteria(criteriaDetails).list(), getEntityClass());
    }

    public <T> List<T> listByQuery(Class<T> returnType, QueryDetails queryDetails) {
        Assert.notNull( returnType );
        Assert.notNull( queryDetails );
        return Collections.checkedList(constructQuery(queryDetails).list(), returnType);
    }

    public List<D> listByQuery(QueryDetails queryDetails) {
        Assert.notNull( queryDetails );
        return Collections.checkedList(constructQuery(queryDetails).list(), getEntityClass());
    }

    public Criteria constructCriteria(CriteriaDetails criteriaDetails) {
        Assert.notNull( criteriaDetails );
        Criteria criteria;
        criteria = (criteriaDetails.getAlias() == null) ? getSession().createCriteria(getEntityClass()) : getSession().createCriteria(getEntityClass(), criteriaDetails.getAlias());
        return criteriaDetails.completeCriteria(criteria).setCacheable(true);
    }

    public Query constructQuery(QueryDetails queryDetails) {
        Assert.notNull( queryDetails );
        return queryDetails.completeQuery(getSession().createQuery(queryDetails.getQueryString()).setCacheable(true));
    }

    public Iterable<D> scrollByCriteria(CriteriaDetails criteriaDetails) {
        Assert.notNull( criteriaDetails );
        return new ScrollIterator<D>(constructCriteria(criteriaDetails).scroll(ScrollMode.SCROLL_INSENSITIVE), getEntityClass());
    }





}