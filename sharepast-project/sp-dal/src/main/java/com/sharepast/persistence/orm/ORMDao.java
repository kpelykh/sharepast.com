package com.sharepast.persistence.orm;

import com.sharepast.persistence.Dao;
import com.sharepast.persistence.Durable;
import org.hibernate.Session;

import java.io.Serializable;
import java.util.List;

public interface ORMDao<I extends Serializable & Comparable<I>, D extends Durable> extends Dao<I, D> {

  public abstract Session getSession ();

  public abstract Class<D> getManagedClass ();

  public abstract D get (I id);

  public abstract D persist (D durable);

  public abstract void delete (D durable);

  public abstract List<D> list ();

  public abstract Iterable<D> scroll (int fetchSize);

  public abstract long size ();
}