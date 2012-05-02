/**
 * (C) Copyright 2010-2012, ZettaSet Inc. All rights reserved.
 * ZettaSet PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sharepast.genericdao.hibernate;

import org.hibernate.Query;

public abstract class QueryDetails {

  public abstract String getQueryString ();

  public Query completeQuery (Query query) {
      return  query;
  }
}
