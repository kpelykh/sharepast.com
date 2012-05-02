/**
 * (C) Copyright 2010-2012, ZettaSet Inc. All rights reserved.
 * ZettaSet PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.com.sharepast.dao;

import com.sharepast.domain.user.User;
import com.sharepast.genericdao.hibernate.GenericDAO;

public interface UserDAO extends GenericDAO<User, Integer> {

  public boolean isUsernameAvailable(final String username);

  public boolean isEmailAvailable(String email);

  public abstract User findByUsername(String username);


}