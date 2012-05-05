package com.sharepast.genericdao.hibernate;

import org.hibernate.Query;

public abstract class QueryDetails {

  public abstract String getQueryString ();

  public Query completeQuery (Query query) {
      return  query;
  }
}
