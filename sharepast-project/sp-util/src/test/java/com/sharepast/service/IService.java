package java.com.sharepast.service;

import com.sharepast.domain.IEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/21/12
 * Time: 11:00 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IService < T extends IEntity,  I extends Serializable & Comparable<I> >{

// find/get

    T findOne( final I id );

    List< T > findAll();

// save/create/persist

    T save( final T entity );

// update/merge

    void update( final T entity );

// delete

    void delete( final I id );

    void delete( final T entities );

}