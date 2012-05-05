package com.sharepast.dao;

import com.sharepast.domain.user.User;
import com.sharepast.genericdao.hibernate.GenericDAO;

public interface UserDAO extends GenericDAO<User, Integer> {

  public boolean isUsernameAvailable(final String username);

  public boolean isEmailAvailable(String email);

  public abstract User findByUsername(String username);

  public abstract User findByEmail(String email);


}