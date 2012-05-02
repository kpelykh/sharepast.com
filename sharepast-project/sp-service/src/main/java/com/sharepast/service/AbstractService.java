package com.sharepast.service;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 1/24/12
 * Time: 12:40 PM
 * To change this template use File | Settings | File Templates.
 */

import com.sharepast.domain.IEntity;
import com.sharepast.genericdao.hibernate.GenericDAO;
import com.sharepast.service.exception.ServiceException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Transactional( propagation = Propagation.REQUIRED )
public abstract class AbstractService< T extends IEntity> implements IService< T, Integer> {

// find/get

    @Override
    @Transactional( readOnly = true )
    public T findOne( final Integer id ) throws ServiceException {
        try {
            return getDao().find(id);
        } catch (Throwable throwable) {
            throw new ServiceException(throwable);

        }
    }

    @Override
    @Transactional( readOnly = true )
    public List< T > findAll() throws ServiceException {
        try {
          return Collections.unmodifiableList(getDao().findAll());
        } catch (Throwable throwable) {
            throw new ServiceException(throwable);
        }
    }

// save/create/persist

    @Override
    public T save( final T entity ) throws ServiceException {
        try {
          getDao().save( entity );
        } catch (Throwable throwable) {
            throw new ServiceException(throwable);
        }
        return entity;
    }

// update/merge

    @Override
    public T update( final T entity ) throws ServiceException {
        try {
          getDao().save( entity );
        } catch (Throwable throwable) {
            throw new ServiceException(throwable);
        }
        return entity;
    }

// delete

    @Override
    public boolean delete(  T  entity ) throws ServiceException {
        try {
          return getDao().remove( entity );
        } catch (Throwable throwable) {
            throw new ServiceException(throwable);
        }
    }

    @Override
    public boolean delete( final Integer id ) throws ServiceException {
        try {
          return getDao().removeById( id );
        } catch (Throwable throwable) {
            throw new ServiceException(throwable);
        }
    }

// template method

    protected abstract GenericDAO<T, Integer> getDao();


}