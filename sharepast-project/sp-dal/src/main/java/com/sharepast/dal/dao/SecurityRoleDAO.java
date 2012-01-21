package com.sharepast.dal.dao;

import com.sharepast.dal.domain.SecurityRole;
import com.sharepast.persistence.orm.ORMDao;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 3/23/11
 * Time: 11:23 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SecurityRoleDAO extends ORMDao<Long, SecurityRole>
{
    public abstract SecurityRole find( String name );
    public abstract SecurityRole findOrCreate( String name );

    public abstract Set<SecurityRole> getRolesForAccount (final long accountId);
}
