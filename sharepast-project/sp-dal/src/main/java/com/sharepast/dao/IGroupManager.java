package com.sharepast.dao;

import com.sharepast.domain.user.Role;
import com.sharepast.persistence.ORMDao;
import org.springframework.security.provisioning.GroupManager;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/21/12
 * Time: 11:06 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IGroupManager extends ORMDao<Role, Long>, GroupManager {

    public Role findByName(String groupName);

}

