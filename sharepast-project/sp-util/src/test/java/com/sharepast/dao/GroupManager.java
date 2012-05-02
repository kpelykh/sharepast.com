/**
 * (C) Copyright 2010-2012, ZettaSet Inc. All rights reserved.
 * ZettaSet PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.com.sharepast.dao;

import com.sharepast.domain.user.Group;
import com.sharepast.domain.user.Permission;
import com.sharepast.genericdao.hibernate.GenericDAO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 1/25/12
 * Time: 2:40 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GroupManager extends GenericDAO<Group, Integer>, org.springframework.security.provisioning.GroupManager {

    public Group findByName(String roleName);

    public List<Permission> findAllPermissions();

}
