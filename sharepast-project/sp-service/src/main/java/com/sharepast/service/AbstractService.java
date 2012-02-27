package com.sharepast.service;

import com.sharepast.domain.IEntity;
import com.sharepast.persistence.ORMDao;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/21/12
 * Time: 11:00 PM
 * To change this template use File | Settings | File Templates.
 */
@Transactional( propagation = Propagation.REQUIRES_NEW )
public abstract class AbstractService< T extends IEntity> implements IService< T,  Long> {

// find/get

    @Override
    @Transactional( readOnly = true )
    public T findOne( final Long id ){
        return getDao().get(id);
    }

    @Override
    @Transactional( readOnly = true )
    public List< T > findAll(){
        return Collections.unmodifiableList(getDao().list());
    }

// save/create/persist

    @Override
    public T save( final T entity ){
        Assert.notNull(entity);

        final T persistedEntity = getDao().persist( entity );

        return persistedEntity;
    }

// update/merge

    @Override
    public void update( final T entity ){
        Assert.notNull( entity );

        getDao().persist( entity );
    }

// delete

    @Override
    public void delete(  T  entity ){
        Assert.notNull(entity);

        getDao().delete( entity );
    }

    @Override
    public void delete( final Long id ){
        getDao().deleteById( id );
    }

// template method

    protected abstract ORMDao< T, Long> getDao();


}