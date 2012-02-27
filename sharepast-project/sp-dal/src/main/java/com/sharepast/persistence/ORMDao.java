package com.sharepast.persistence;

import com.sharepast.domain.IEntity;
import org.hibernate.Session;

import java.io.Serializable;
import java.util.List;

public interface ORMDao <D extends IEntity, I extends Serializable & Comparable<I>> {

    public abstract Session getSession();

    public abstract Class<D> getEntityClass();

    public abstract D get(I id);

    public abstract D persist(D durable);

    public abstract void delete(D durable);

    public abstract void deleteById(I id);

    public abstract List<D> list();

    public abstract Iterable<D> scroll(int fetchSize);

    public abstract long size();
}
