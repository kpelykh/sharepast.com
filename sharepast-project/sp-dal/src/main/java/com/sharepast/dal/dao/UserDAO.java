package com.sharepast.dal.dao;

import com.sharepast.dal.domain.user.User;
import com.sharepast.persistence.orm.ORMDao;

import java.util.List;

public interface UserDAO extends ORMDao<Long, User> {

  User getUser(Long userId);

  User findUser(String username);

  void createUser(User user);

  List<User> getAllUsers();

  void deleteUser(Long userId);

  void updateUser(User user);

  public abstract User findByEmail (String email);

  /**
   * find accounts matching email regex
   */
  public abstract List<User> findByEmailRegEx (String email);


}