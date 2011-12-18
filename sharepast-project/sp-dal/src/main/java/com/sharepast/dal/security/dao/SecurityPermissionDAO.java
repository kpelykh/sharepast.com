package com.sharepast.dal.security.dao;

import com.sharepast.dal.security.domain.SecurityPermission;
import com.sharepast.persistence.orm.ORMDao;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 3/23/11
 * Time: 11:32 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SecurityPermissionDAO extends ORMDao<Long, SecurityPermission>
{
  public abstract SecurityPermission find( String text );
  public abstract SecurityPermission findOrCreate( String text );

  public abstract List<SecurityPermission> getPermissionsForRole(final long roleId);
}