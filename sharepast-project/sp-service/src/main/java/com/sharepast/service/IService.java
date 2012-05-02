package com.sharepast.service;

import com.sharepast.domain.IEntity;
import com.sharepast.service.exception.ServiceException;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 1/24/12
 * Time: 12:40 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IService < T extends IEntity,  I extends Serializable & Comparable<I>>{

// find/get

    T findOne(final I id) throws ServiceException;

    List< T > findAll() throws ServiceException;

// save/create/persist

    T save(final T entity) throws ServiceException;

// update/merge

    T update(final T entity) throws ServiceException;

// delete

    /**
     * Remove the entity with the specified id from the datastore.
     *
     * @return <code>true</code> if the entity is found in the datastore and
     *         removed, <code>false</code> if it is not found.
     */
    boolean delete(final I id) throws ServiceException;

    /**
     * Remove the entity from the datastore.
     *
     * @return <code>true</code> if the entity is found in the datastore and
     *         removed, <code>false</code> if it is not found.
     */
    boolean delete(final T entity) throws ServiceException;

}